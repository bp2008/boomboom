package com.brian.boomboom.pathfinder;

import java.util.ArrayList;

import com.brian.boomboom.util.Point;
import com.brian.boomboom.world.Square;

//
//  THIS CODE AND INFORMATION IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
//  KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
//  IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR
//  PURPOSE. IT CAN BE DISTRIBUTED FREE OF CHARGE AS LONG AS THIS HEADER 
//  REMAINS UNCHANGED.
//
//  Email:  gustavo_franco@hotmail.com
//
//  Copyright (C) 2006 Franco, Gustavo 
//
// ======================================================================
//
// Modified and ported to Java in 2012 by Brian Pearce
//
// ======================================================================

    //[Author("Franco, Gustavo")]
    public class PathFinderFast implements IPathFinder
    {
        // Heap variables are initializated to default, but I like to do it anyway
        private Square[][]                       mGrid                   = null;
        private PriorityQueueB<Integer>             mOpen                   = null;
        private ArrayList<PathFinderNode>            mClose                  = new ArrayList<PathFinderNode>();
        private boolean                            mStop                   = false;
        private boolean                            mStopped                = true;
        private int                             mHoriz                  = 0;
        public HeuristicFormula                mFormula                = HeuristicFormula.Manhattan;
        private boolean                            mDiagonals              = true;
        public int                             mHEstimate              = 2;
        public boolean                            mPunishChangeDirection  = false;
        public boolean                            mTieBreaker             = false;
        public boolean                            mHeavyDiagonals         = false;
        public int                             mSearchLimit            = 2000;
        public boolean                            mDebugProgress          = false;
        public boolean                            mDebugFoundPath         = false;
        private PathFinderNodeFast[]            mCalcGrid               = null;
        private byte                            mOpenNodeValue          = 1;
        private byte                            mCloseNodeValue         = 2;
        
        //Promoted local variables to member variables to avoid recreation between calls
        private int                             mH                      = 0;
        private int                             mLocation               = 0;
        private int                             mNewLocation            = 0;
        private int                          mLocationX              = 0;
        private int                          mLocationY              = 0;
        private int                          mNewLocationX           = 0;
        private int                          mNewLocationY           = 0;
        private int                             mCloseNodeCounter       = 0;
        private int                          mGridX                  = 0;
        private int                          mGridY                  = 0;
        private int                          mGridXMinus1            = 0;
        private int                          mGridYLog2              = 0;
        private boolean                            mFound                  = false;
        private byte[][]                        mDirection              = new byte[][]{{0,-1} , {1,0}, {0,1}, {-1,0}, {1,-1}, {1,1}, {-1,1}, {-1,-1}};
        private int                             mEndLocation            = 0;
        private int                             mNewG                   = 0;

        public void SetGrid(Square[][] grid)
		{
			mGrid = grid;
		}
        public PathFinderFast(Square[][] grid) throws Exception
        {
            if (grid == null)
                throw new Exception("Grid cannot be null");

            mGrid           = grid;
            mGridX          = mGrid.length;
			mGridY			= mGrid[0].length;
			int mGridMaxval = Math.max(mGridX, mGridY);
			double tempLog	= Math.log(mGridMaxval) / Math.log(2);
			mGridYLog2		= (int)Math.ceil(tempLog);
			mGridXMinus1	= (int)(Math.pow(2, mGridYLog2) - 1);

			//// This should be done at the constructor, for now we leave it here.
			//if (Math.Log(mGridX, 2) != (int) Math.Log(mGridX, 2) ||
			//    Math.Log(mGridY, 2) != (int) Math.Log(mGridY, 2))
			//    throw new Exception("Invalid Grid, size in X and Y must be power of 2");

			if (mCalcGrid == null || mCalcGrid.length != ((mGridXMinus1 + 1) * (mGridXMinus1 + 1)))
			{
				int sizeArray = (mGridXMinus1 + 1) * (mGridXMinus1 + 1);
				mCalcGrid = new PathFinderNodeFast[sizeArray];
				for(int i = 0; i < mCalcGrid.length; i++)
					mCalcGrid[i] = new PathFinderNodeFast();
			}

            mOpen   = new PriorityQueueB<Integer>(new ComparePFNodeMatrix(mCalcGrid));
        }

        public boolean getStopped()
        {
            return mStopped;
        }
        
        public boolean getDiagonals()
        {
            return mDiagonals;
        }
        public void setDiagonals(boolean value)
        { 
            mDiagonals = value; 
            if (mDiagonals)
                mDirection = new byte[][]{{0,-1} , {1,0}, {0,1}, {-1,0}, {1,-1}, {1,1}, {-1,1}, {-1,-1}};
            else
                mDirection = new byte[][]{{0,-1} , {1,0}, {0,1}, {-1,0}};
        }

        public void FindPathStop()
        {
            mStop = true;
        }

        public ArrayList<PathFinderNode> FindPath(Point start, Point end)
        {
            synchronized(this)
            {
                // Is faster if we don't clear the matrix, just assign different values for open and close and ignore the rest
                // I could have user Array.Clear() but using unsafe code is faster, no much but it is.
                //fixed (PathFinderNodeFast* pGrid = tmpGrid) 
                //    ZeroMemory((byte*) pGrid, sizeof(PathFinderNodeFast) * 1000000);

                mFound              = false;
                mStop               = false;
                mStopped            = false;
                mCloseNodeCounter   = 0;
                mOpenNodeValue      += 2;
                mCloseNodeValue     += 2;
                mOpen.Clear();
                mClose.clear();

                mLocation                      = (start.y << mGridYLog2) + start.x;
                mEndLocation                   = (end.y << mGridYLog2) + end.x;
                mCalcGrid[mLocation].G         = 0;
                mCalcGrid[mLocation].F         = mHEstimate;
                mCalcGrid[mLocation].PX        = start.x;
                mCalcGrid[mLocation].PY        = start.y;
                mCalcGrid[mLocation].Status    = mOpenNodeValue;

                mOpen.Push(mLocation);
                while(mOpen.Count() > 0 && !mStop)
                {
                    mLocation    = mOpen.Pop();

                    //Is it in closed list? means this node was already processed
                    if (mCalcGrid[mLocation].Status == mCloseNodeValue)
                        continue;

                    mLocationX   = (int) (mLocation & mGridXMinus1);
                    mLocationY   = (int) (mLocation >> mGridYLog2);

                    if (mLocation == mEndLocation)
                    {
                        mCalcGrid[mLocation].Status = mCloseNodeValue;
                        mFound = true;
                        break;
                    }

                    if (mCloseNodeCounter > mSearchLimit)
                    {
                        mStopped = true;
                        return null;
                    }

                    if (mPunishChangeDirection)
                        mHoriz = (mLocationX - mCalcGrid[mLocation].PX); 

                    //Lets calculate each successors
                    for (int i=0; i<(mDiagonals ? 8 : 4); i++)
                    {
                        mNewLocationX = (int) (mLocationX + mDirection[i][0]);
                        mNewLocationY = (int) (mLocationY + mDirection[i][1]);
                        mNewLocation  = (mNewLocationY << mGridYLog2) + mNewLocationX;

                        if (mNewLocationX >= mGridX || mNewLocationY >= mGridY || mNewLocationX < 0 || mNewLocationY < 0)
                            continue;

                        // Unbreakeable?
                        int pathFindValue = mGrid[mNewLocationX][mNewLocationY].PathFindingValue();
                        if (pathFindValue == 0)
                            continue;

                        if (mHeavyDiagonals && i>3)
							mNewG = mCalcGrid[mLocation].G + (int)(pathFindValue * 2.41);
                        else
							mNewG = mCalcGrid[mLocation].G + pathFindValue;

                        if (mPunishChangeDirection)
                        {
                            if ((mNewLocationX - mLocationX) != 0)
                            {
                                if (mHoriz == 0)
                                    mNewG += Math.abs(mNewLocationX - end.x) + Math.abs(mNewLocationY - end.y);
                            }
                            if ((mNewLocationY - mLocationY) != 0)
                            {
                                if (mHoriz != 0)
                                    mNewG += Math.abs(mNewLocationX - end.x) + Math.abs(mNewLocationY - end.y);
                            }
                        }

                        //Is it open or closed?
                        if (mCalcGrid[mNewLocation].Status == mOpenNodeValue || mCalcGrid[mNewLocation].Status == mCloseNodeValue)
                        {
                            // The current node has less code than the previous? then skip this node
                            if (mCalcGrid[mNewLocation].G <= mNewG)
                                continue;
                        }

                        mCalcGrid[mNewLocation].PX      = mLocationX;
                        mCalcGrid[mNewLocation].PY      = mLocationY;
                        mCalcGrid[mNewLocation].G       = mNewG;

                        switch(mFormula)
                        {
                            default:
                            case Manhattan:
                                mH = mHEstimate * (Math.abs(mNewLocationX - end.x) + Math.abs(mNewLocationY - end.y));
                                break;
                            case MaxDXDY:
                                mH = mHEstimate * (Math.max(Math.abs(mNewLocationX - end.x), Math.abs(mNewLocationY - end.y)));
                                break;
                            case DiagonalShortCut:
                                int h_diagonal  = Math.min(Math.abs(mNewLocationX - end.x), Math.abs(mNewLocationY - end.y));
                                int h_straight  = (Math.abs(mNewLocationX - end.x) + Math.abs(mNewLocationY - end.y));
                                mH = (mHEstimate * 2) * h_diagonal + mHEstimate * (h_straight - 2 * h_diagonal);
                                break;
                            case Euclidean:
                                mH = (int) (mHEstimate * Math.sqrt(Math.pow((mNewLocationY - end.x) , 2) + Math.pow((mNewLocationY - end.y), 2)));
                                break;
                            case EuclideanNoSQR:
                                mH = (int) (mHEstimate * (Math.pow((mNewLocationX - end.x) , 2) + Math.pow((mNewLocationY - end.y), 2)));
                                break;
                            case Custom1:
                                Point dxy       = new Point(Math.abs(end.x - mNewLocationX), Math.abs(end.y - mNewLocationY));
                                int Orthogonal  = Math.abs(dxy.x - dxy.y);
                                int Diagonal    = Math.abs(((dxy.x + dxy.y) - Orthogonal) / 2);
                                mH = mHEstimate * (Diagonal + Orthogonal + dxy.x + dxy.y);
                                break;
                        }
                        if (mTieBreaker)
                        {
                            int dx1 = mLocationX - end.x;
                            int dy1 = mLocationY - end.y;
                            int dx2 = start.x - end.x;
                            int dy2 = start.y - end.y;
                            int cross = Math.abs(dx1 * dy2 - dx2 * dy1);
                            mH = (int) (mH + cross * 0.001);
                        }
                        mCalcGrid[mNewLocation].F = mNewG + mH;

                        //It is faster if we leave the open node in the priority queue
                        //When it is removed, it will be already closed, it will be ignored automatically
                        //if (tmpGrid[newLocation].Status == 1)
                        //{
                        //    //int removeX   = newLocation & gridXMinus1;
                        //    //int removeY   = newLocation >> gridYLog2;
                        //    mOpen.RemoveLocation(newLocation);
                        //}

                        //if (tmpGrid[newLocation].Status != 1)
                        //{
                            mOpen.Push(mNewLocation);
                        //}
                        mCalcGrid[mNewLocation].Status = mOpenNodeValue;
                    }

                    mCloseNodeCounter++;
                    mCalcGrid[mLocation].Status = mCloseNodeValue;
                }

                if (mFound)
                {
                    mClose.clear();
                    int posX = end.x;
                    int posY = end.y;

                    PathFinderNodeFast fNodeTmp = mCalcGrid[(end.y << mGridYLog2) + end.x];
                    PathFinderNode fNode = new PathFinderNode();
                    fNode.F  = fNodeTmp.F;
                    fNode.G  = fNodeTmp.G;
                    fNode.H  = 0;
                    fNode.PX = fNodeTmp.PX;
                    fNode.PY = fNodeTmp.PY;
                    fNode.X  = end.x;
                    fNode.Y  = end.y;

                    while(fNode.X != fNode.PX || fNode.Y != fNode.PY)
                    {
                        mClose.add(fNode.copy());

                        posX = fNode.PX;
                        posY = fNode.PY;
                        fNodeTmp = mCalcGrid[(posY << mGridYLog2) + posX];
                        fNode.F  = fNodeTmp.F;
                        fNode.G  = fNodeTmp.G;
                        fNode.H  = 0;
                        fNode.PX = fNodeTmp.PX;
                        fNode.PY = fNodeTmp.PY;
                        fNode.X  = posX;
                        fNode.Y  = posY;
                    } 

                    mClose.add(fNode.copy());

                    mStopped = true;
                    return mClose;
                }
                mStopped = true;
                return null;
            }
        }
    }
