package com.brian.boomboom.pathfinder;

import java.util.ArrayList;



//[Author("Franco, Gustavo")]
public class PriorityQueueB<T> implements IPriorityQueue<T>
{
    protected ArrayList<T> InnerList = new ArrayList<T>();
	protected IComparer<T> mComparer;

    public PriorityQueueB(IComparer<T> comparer)
	{
		mComparer = comparer;
	}

	public PriorityQueueB(IComparer<T> comparer, int capacity)
	{
		mComparer = comparer;
		InnerList.ensureCapacity(capacity);
	}
    protected void SwitchElements(int i, int j)
	{
		T h = InnerList.get(i);
		InnerList.set(i, InnerList.get(j));
		InnerList.set(j, h);
	}

    protected int OnCompare(int i, int j)
    {
        return mComparer.Compare(InnerList.get(i), InnerList.get(j));
    }

	/// <summary>
	/// Push an object onto the PQ
	/// </summary>
	/// <param name="O">The new object</param>
	/// <returns>The index in the list where the object is _now_. This will change when objects are taken from or put onto the PQ.</returns>
	public int Push(T item)
	{
		int p = InnerList.size(), p2;
		InnerList.add(item); // E[p] = O
		do
		{
			if(p==0)
				break;
			p2 = (p-1)/2;
			if(OnCompare(p,p2)<0)
			{
				SwitchElements(p,p2);
				p = p2;
			}
			else
				break;
		}while(true);
		return p;
	}

	/// <summary>
	/// Get the smallest object and remove it.
	/// </summary>
	/// <returns>The smallest object</returns>
	public T Pop()
	{
		T result = InnerList.get(0);
		int p = 0,p1,p2,pn;
		InnerList.set(0, InnerList.get(InnerList.size()-1));
		InnerList.remove(InnerList.size()-1);
		do
		{
			pn = p;
			p1 = 2*p+1;
			p2 = 2*p+2;
			if(InnerList.size()>p1 && OnCompare(p,p1)>0) // links kleiner
				p = p1;
			if(InnerList.size()>p2 && OnCompare(p,p2)>0) // rechts noch kleiner
				p = p2;
			
			if(p==pn)
				break;
			SwitchElements(p,pn);
		}while(true);

        return result;
	}

	/// <summary>
	/// Notify the PQ that the object at position i has changed
	/// and the PQ needs to restore order.
	/// Since you dont have access to any indexes (except by using the
	/// explicit IList.this) you should not call this function without knowing exactly
	/// what you do.
	/// </summary>
	/// <param name="i">The index of the changed object.</param>
	public void Update(int i)
	{
		int p = i,pn;
		int p1,p2;
		do	// aufsteigen
		{
			if(p==0)
				break;
			p2 = (p-1)/2;
			if(OnCompare(p,p2)<0)
			{
				SwitchElements(p,p2);
				p = p2;
			}
			else
				break;
		}while(true);
		if(p<i)
			return;
		do	   // absteigen
		{
			pn = p;
			p1 = 2*p+1;
			p2 = 2*p+2;
			if(InnerList.size()>p1 && OnCompare(p,p1)>0) // links kleiner
				p = p1;
			if(InnerList.size()>p2 && OnCompare(p,p2)>0) // rechts noch kleiner
				p = p2;
			
			if(p==pn)
				break;
			SwitchElements(p,pn);
		}while(true);
	}

	/// <summary>
	/// Get the smallest object without removing it.
	/// </summary>
	/// <returns>The smallest object</returns>
	public T Peek()
	{
		if(InnerList.size()>0)
			return InnerList.get(0);
		return null;
	}

	public void Clear()
	{
		InnerList.clear();
	}

	public int Count()
	{
		return InnerList.size();
	}
	public int size()
	{
		return InnerList.size();
	}

    public void RemoveLocation(T item)
    {
        int index = -1;
        for(int i=0; i<InnerList.size(); i++)
        {
            
            if (mComparer.Compare(InnerList.get(i), item) == 0)
                index = i;
        }

        if (index != -1)
            InnerList.remove(index);
    }

    public T get(int index)
    {
        return InnerList.get(index);
    }
    public void set(int index, T value)
    {
    	InnerList.set(index,  value);
    	Update(index);
    }
}
