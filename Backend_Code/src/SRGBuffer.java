package src;


public class SRGBuffer {
 
    private int[] queue;     
    private int head = 0;       
    private int tail = 0;
    private int count = 0;
    private int capacity;
 
    public SRGBuffer(int capacity) {
        this.capacity = capacity;
        this.queue = new int[capacity];
    }
 
    //o producer prosthetei arithmo sthn oura
    public synchronized void enqueue(int num) throws InterruptedException {
        while (count == capacity) {  //queue is full
            wait();   
        }
        queue[tail] = num;
        tail = (tail + 1) % capacity; //circular movement
        count++;
        notifyAll(); //consumer wakes up
    }
 
    // o consumer afairei arithmo apo thn oura
    public synchronized int dequeue() throws InterruptedException {
        while (count == 0) {
            wait();                       
        }
        int number = queue[head];
        head = (head + 1) % capacity;
        count--;
        notifyAll();  //producer wakes up
        return number;
    }
 
    public synchronized int size(){
        return count;
    }
    public synchronized boolean isEmpty(){
        return count == 0;
    }
    public synchronized boolean isFull(){
        return count == capacity;
    }
}