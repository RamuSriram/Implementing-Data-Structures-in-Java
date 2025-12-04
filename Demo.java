import java.util.Arrays;
import java.util.HashSet;

class CustomHashSet {
    // How does an HashSet work? It basically works on the principle of hashing and
    // Hashtables.
    // Hash = Takes input, gives output, but here. the goal of an hash function is
    // to generate an integer as an hash value because we are going to use it as an
    // index value
    // For simplicity, we are going to keep our current Custom HashSet class only
    // allow integers to be used to store the elements in our hashset.
    int dataArray[];
    int capacity = 0;
    int count = 0;

    CustomHashSet(int initialCapacity) {
        dataArray = new int[initialCapacity];
        Arrays.fill(dataArray, Integer.MIN_VALUE);
        this.capacity = initialCapacity;
    }

    void add(int elem) {
        int hash = baseHash(elem, capacity);
        for (int i = 0; i < capacity; i++) {
            int probe = (hash + i) % capacity;
            if (dataArray[probe] == Integer.MIN_VALUE) {
                dataArray[probe] = elem;
                this.count++;
                return;
            } else if (dataArray[probe] == elem) {
                return;
            }
        }
        // System.out.println("No empty slot found inserting your element: " + elem); // Let's no longer throw this error to the console, instead, let's do the real thing which is resizing
        resize();
        hash = baseHash(elem, capacity); // we are just copy pasting the above code cuz we want to redo the same thing after resizing and rehashing, cuz we would find an empty slot for sure this time
        for (int i = 0; i < capacity; i++) {
            int probe = (hash + i) % capacity;
            if (dataArray[probe] == Integer.MIN_VALUE) {
                dataArray[probe] = elem;
                this.count++;
                return;
            } else if (dataArray[probe] == elem) {
                return;
            }
        }
    }

    boolean contains(int num) {
        int hash = baseHash(num, capacity);
        if (dataArray[hash] == num) { 
            return true;
        } else { // the linear probing part
            for (int i = 1; i < capacity; i++) { //I know that I can completely remove if else and just keep the loop with i starting as 0, but again just for my own understanding when I look back the code.
                int probe = (hash + i) % capacity;
                if (dataArray[probe] == num ) {
                    return true;
                } else if (dataArray[probe] == Integer.MIN_VALUE) {
                    return false;
                }
            }
        }
        return false;
    }

    int baseHash(int elem, int capacity) { //  I know that we don't have to mention capacity parameter always as that property already exists in that class, but I just want keep it as is cuz when I relook the solution, I would understand it intuitively.
        int result = elem % capacity;
        if (result < 0) {
            return result + capacity;
        }
        return result;
    }

    int getSize() {
        return count;
    }

    @Override
    public String toString() {
        // return super.toString();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dataArray.length; i++) {
            if (dataArray[i] != Integer.MIN_VALUE) {
                sb.append(dataArray[i] + ":" + i + " ");
            }
        }
        return sb.toString();
    }

    void resize(){ // let's do the resizing and rehashing thing when our array would get filled/about to get filled
        int newCapacity = capacity * 2;
        int newArray[] = new int[newCapacity];
        Arrays.fill(newArray, Integer.MIN_VALUE);
        for(int i=0; i<dataArray.length; i++){
            int temp = dataArray[i];
            int newHash = baseHash(temp, newCapacity);
            for (int x = 0; x < newCapacity; x++) {
                int probe = (newHash + x) % newCapacity;
                if (newArray[probe] == Integer.MIN_VALUE) {
                    newArray[probe] = temp;
                    break;
                } 
            }
        }
        dataArray = newArray;
        capacity = newCapacity;
    }

    void printDataArray(){ // Why not print raw data array for debugging? Seems like a good idea
        for(int i=0; i<dataArray.length; i++){
            System.out.print(dataArray[i] + ", ");
        }
        System.out.println();
    }
}

class Demo {
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
        mySet.printDataArray();
    }
}