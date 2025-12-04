import java.util.Arrays;
import java.util.HashSet;
class CustomHashSet{
    // How does an HashSet work? It basically works on the principle of hashing and Hashtables. 
    // Hash = Takes input, gives output, but here. the goal of an hash function is to generate an integer as an hash value because we are going to use it as an index value
    // For simplicity, we are going to keep our current Custom HashSet class only allow integers to be used to store the elements in our hashset.
    int dataArray[];
    int capacity = 0;
    int count = 0;
    CustomHashSet(int capacity){
        dataArray = new int[capacity];
        Arrays.fill(dataArray, Integer.MIN_VALUE);
        this.capacity = capacity;
    }
    
    void add(int elem){
        int hash = hashFunc(elem, capacity);
        int curPointer = hash;
        while (curPointer < capacity*2) { // why not apply the same logic that we used in contains method? it's smart to just check until capacity*2
            if(dataArray[curPointer % capacity] == Integer.MIN_VALUE || dataArray[curPointer % capacity] == elem){
                dataArray[curPointer % capacity] = elem;
                this.count++;
                return;
            }
            curPointer++;
        }
        System.out.println("No empty slot found inserting your element " + elem);
    }

    boolean contains(int num){
        if(dataArray[hashFunc(num, capacity)] == num){ 
            return true;
        }
        else{ //the linear probing part 
            int curPointer = hashFunc(num, capacity);
            while(curPointer < capacity*2){ //I am doing capacity*2 here cuz I know that if the curPointer has reached beyond that, we didn't find that element anywhere
                if(dataArray[curPointer%capacity] == num){
                    return true;
                }
                curPointer++;
            }
        }
        return false;
    }

    int hashFunc(int elem, int capacity){
        int result = elem % capacity;
        // //Linear Probing // Making the linear probing logic as method specific, else our code would turn into a bug zoo lol
        // if(dataArray[result] != Integer.MIN_VALUE){
        //     int curPointer = result;
        //     while (curPointer < capacity*2) { // why not apply the same logic that we used in contains method? it's smart to just check until capacity*2
        //         if(dataArray[curPointer % capacity] == Integer.MIN_VALUE || dataArray[curPointer % capacity] == elem){
        //             return curPointer%capacity;
        //         }
        //         curPointer++;
        //     }
        //     return Integer.MIN_VALUE;
        // }
        return result;
    }
    int getSize(){
        return count;
    }
    @Override
    public String toString() {
        // return super.toString();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<dataArray.length; i++){
            if(dataArray[i] != Integer.MIN_VALUE){
                sb.append(dataArray[i] + ":" + i + " ");
            }
        }
        return sb.toString();
    }
}
class Demo{
    public static void main(String[] args) {
        HashSet<Integer> set = new HashSet<>();
        set.add(1);
        set.add(23);
        set.add(5);
        set.add(110);
        System.out.println(set);
        CustomHashSet mySet = new CustomHashSet(10);
        mySet.add(13);
        mySet.add(24);
        mySet.add(43);
        mySet.add(46);
        mySet.add(48);
        mySet.add(1);
        mySet.add(2);
        mySet.add(3);
        mySet.add(4);
        mySet.add(5);
        mySet.add(9);
        System.out.println(mySet);
        System.out.println(mySet.contains(43));
        System.out.println(mySet.contains(79));
        System.out.println(mySet.contains(46));
        System.out.println(mySet.count);
    }
}