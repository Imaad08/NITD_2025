package com.nighthawk.spring_portfolio.hacks.classDataStruct;
/**
 *  Implementation of a Double Linked List;  forward and backward links point to adjacent Nodes.
 *
 */

public class LinkedList<T>
{
    private T data;
    private LinkedList<T> prevNode, nextNode;

    /**
     *  Constructs a new element
     *
     * @param  data, data of object
     * @param  node, previous node
     */
    public LinkedList(T data, LinkedList<T> node)
    {
        this.setData(data);
        this.setPrevNode(node);
        this.setNextNode(null);
    }

    /**
     *  Clone an object,
     *
     * @param  node  object to clone
     */
    public LinkedList(LinkedList<T> node)
    {
        this.setData(node.data);
        this.setPrevNode(node.prevNode);
        this.setNextNode(node.nextNode);
    }

    /**
     *  Setter for T data in DoubleLinkedNode object
     *
     * @param  data, update data of object
     */
    public void setData(T data)
    {
        this.data = data;
    }

    /**
     *  Returns T data for this element
     *
     * @return  data associated with object
     */
    public T getData()
    {
        return this.data;
    }

    /**
     *  Setter for prevNode in DoubleLinkedNode object
     *
     * @param node, prevNode to current Object
     */
    public void setPrevNode(LinkedList<T> node)
    {
        this.prevNode = node;
    }

    /**
     *  Setter for nextNode in DoubleLinkedNode object
     *
     * @param node, nextNode to current Object
     */
    public void setNextNode(LinkedList<T> node)
    {
        this.nextNode = node;
    }


    /**
     *  Returns reference to previous object in list
     *
     * @return  the previous object in the list
     */
    public LinkedList<T> getPrevious()
    {
        return this.prevNode;
    }

    /**
     *  Returns reference to next object in list
     *
     * @return  the next object in the list
     */
    public LinkedList<T> getNext()
    {
        return this.nextNode;
    }

}
