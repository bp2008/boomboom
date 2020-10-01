package com.brian.boomboom.pathfinder;

//[Author("Franco, Gustavo")]
public interface IPriorityQueue<T>
{
	int Push(T item);

	T Pop();

	T Peek();

	void Update(int i);
}