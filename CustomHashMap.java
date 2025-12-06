public class CustomHashMap {
    // We are currently building an HashMap that only supports integers as its keys and values
    int dataArray[][];
    int capacity;
    int keyCount;
    private static final int EMPTY  = Integer.MIN_VALUE;
    private static final int DELETED  = Integer.MIN_VALUE + 1; // Tombstones
    CustomHashMap(int initialCapacity){
        dataArray = new int[initialCapacity][2];
        fillWithEmpty(dataArray);
        this.capacity = initialCapacity;
    }
    void fillWithEmpty(int arr[][]){ // Method used to fill the 0th index of each sub index with EMPTY (Integer.MIN_VALUE)
        for(int i=0; i<arr.length; i++){
            arr[i][0] = EMPTY;
        }
    }

    void put(int key, int value){
        int hash = baseHash(key, capacity);
        int saveIndex = -1;
        for(int i=0; i<capacity; i++){
            int probe = (hash + i) % capacity;
            if(dataArray[probe][0] == EMPTY){
                if(saveIndex == -1){
                    saveIndex = probe;
                }
                break;
            }
            else if(dataArray[probe][0] == DELETED){
                if(saveIndex == -1){
                    saveIndex = probe;
                }
            }
            else if(dataArray[probe][0] == key){
                dataArray[probe][1] = value;
                return;
            }
        }
        if(saveIndex!=-1){
            dataArray[saveIndex][0] = key;
            dataArray[saveIndex][1] = value;
            keyCount++;
        }
    }

    int get(int key){
        int hash = baseHash(key, capacity);
        for(int i=0; i<capacity; i++){
            int probe = (hash + i) % capacity;
            if(dataArray[probe][0] == key){
                return dataArray[probe][1];
            }
            else if(dataArray[probe][0] == EMPTY){
                return -1; // not found
            }
        }
        return -1;
    }

    void remove(int key){
        int hash = baseHash(key, capacity);
        for(int i=0; i<capacity; i++){
            int probe = (hash + i) % capacity;
            if(dataArray[probe][0] == key){
                dataArray[probe][0] = DELETED;
                dataArray[probe][1] = 0;  
                keyCount--;
                return;
            }
            else if(dataArray[probe][0] == EMPTY){
                break;
            }
        }
        System.out.println("Key doesn't exist");
    }

    int baseHash(int elem, int capacity){
        int result = elem % capacity;
        if(result < 0){
            return result + capacity;
        }
        return result;
    }

    int getKeyCount(){
        return keyCount;
    }

    @Override
    public String toString() { // Print raw data arrayv elems, we will do checks later
        StringBuilder str = new StringBuilder();
        for(int i=0; i<dataArray.length; i++){
            str.append("[" + dataArray[i][0] + ": " + dataArray[i][1] +  "], ");
        }
        return str.toString();
    }
}

class Demo1{
    public static void main(String[] args) {
        System.out.println("Hello");
        CustomHashMap map = new CustomHashMap(10);
        map.put(12, 1000);
        map.put(23, 230);
        map.put(18, 500);
        System.out.println(map.get(23));
        map.remove(23);
        System.out.println(map.get(23));
        System.out.println(map);
    }
}