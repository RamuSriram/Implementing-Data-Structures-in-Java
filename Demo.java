import java.util.HashSet;
class CustomHashSet{
    // How does an HashSet work? It basically works on the principle of hashing and Hashtables. 
    // Hash = Takes input, gives output, but here. the goal of an hash function is to generate an integer as an hash value because we are going to use it as an index value
    // For simplicity, we are going to keep our current Custom HashSet class only allow integers to be used to store the elements in our hashset.
    int dataArray[];
    int size = 0;
    CustomHashSet(int size){
        dataArray = new int[size];
        this.size = size;
    }
    
    void add(int elem){
        int hash = hashFunc(elem, size);
        dataArray[hash] = elem;
    }

    boolean contains(int num){
        if(dataArray[hashFunc(num, size)] == num){ 
            return true;
        }
        return false;
    }

    int hashFunc(int elem, int size){
        int result = elem % size;
        return result;
    }

    @Override
    public String toString() {
        // return super.toString();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<dataArray.length; i++){
            if(dataArray[i] > 0){ // I know it wouldn't work if there's any zero value in the set, but again, I am just building an MVP
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
        System.out.println(mySet);
    }
}