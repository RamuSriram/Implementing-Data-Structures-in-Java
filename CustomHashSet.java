import java.util.Arrays;
import java.util.HashSet;

public class CustomHashSet {
    /*
        CustomHashSet: my own HashSet implementation using open addressing + linear probing.

        - Backing storage: int[] dataArray
        - Only supports int values (not Integer objects, no generics yet).
        - Uses sentinel values:
            Integer.MIN_VALUE     -> EMPTY slot (no element has ever lived here)
            Integer.MIN_VALUE + 1 -> DELETED slot (an element used to live here, but was removed)

        Important:
        - Because of these sentinels, this set CANNOT safely store:
            Integer.MIN_VALUE or Integer.MIN_VALUE + 1 as real elements.
    */

    int dataArray[];
    int capacity = 0;   // current length of dataArray
    int count = 0;      // number of REAL elements stored (ignores EMPTY and DELETED)

    CustomHashSet(int initialCapacity) {
        dataArray = new int[initialCapacity];
        Arrays.fill(dataArray, Integer.MIN_VALUE); // mark all slots as EMPTY
        this.capacity = initialCapacity;
    }

    void add(int elem) {
        // Load factor = count / capacity.
        // When it grows too large, linear probing chains get long and slow.
        // Here I trigger a resize when load factor >= 0.75 (similar to Java's HashMap).
        // Note: this uses only 'count' (live elements), not tombstones.
        // System.out.println("Current loadfactor is: " + ((count * 1.0)/capacity));
        if ((((count * 1.0) / capacity)) >= 0.75) {
            System.out.println("Current loadfactor is: " + (((count * 1.0)/capacity)) + ", hence we are resizing dataArray now."); // debug info
            resize();
        }

        int hash = baseHash(elem, capacity); // starting index for probe sequence
        int saveIndex = -1;                  // index where we will eventually insert:
                                             // first DELETED or EMPTY slot in the probe chain

        // Linear probing:
        // try hash, hash+1, hash+2, ... wrapping around with % capacity
        for (int i = 0; i < capacity; i++) {
            int probe = (hash + i) % capacity;

            if (dataArray[probe] == Integer.MIN_VALUE) {
                // Case 1: hit a truly EMPTY slot.
                // At this point we know 'elem' does NOT exist in the table:
                // if it did, we would have seen it earlier in this chain.
                //
                // If we never saw a tombstone before, this EMPTY is our best insertion spot.
                // If we did see a tombstone, we still prefer the tombstone (saveIndex already set).
                if (saveIndex == -1) {
                    saveIndex = probe; // remember this EMPTY as candidate insertion position
                }
                break; // we can stop probing; nothing beyond this EMPTY can be 'elem'
            }
            else if (dataArray[probe] == Integer.MIN_VALUE + 1) {
                // Case 2: DELETED (tombstone) slot.
                // For searching, we MUST continue probing, because original chains
                // can continue past tombstones.
                //
                // For insertion, we note the FIRST tombstone we see and try to reuse it,
                // but we still have to keep scanning in case 'elem' already exists later.
                if (saveIndex == -1) {
                    saveIndex = probe; // first reusable slot (tombstone)
                }
            }
            else if (dataArray[probe] == elem) {
                // Case 3: 'elem' already exists in the set.
                // Set semantics: no duplicates, so we just return.
                System.out.println("Element already exists: " + elem);
                return;
            }
            // Any other value: it's some other real element, so we keep probing.
        }

        // After the probing loop:
        // - If saveIndex is still -1, it means the table is somehow full of real elements
        //   (should not happen with our resize logic).
        // - If saveIndex != -1, it is either:
        //      * the first tombstone we saw, or
        //      * the first EMPTY (if no tombstone was seen)
        //   In both cases, we can safely insert 'elem' there.
        if (saveIndex != -1) {
            dataArray[saveIndex] = elem;
            this.count++;
        }
    }

    boolean contains(int num) {
        int hash = baseHash(num, capacity);

        // First check the base index directly (fast path).
        if (dataArray[hash] == num) {
            return true;
        } else {
            // Then linearly probe the rest of the cluster.
            // I start from i = 1 because i = 0 (base index) is already checked.
            for (int i = 1; i < capacity; i++) {
                int probe = (hash + i) % capacity;

                if (dataArray[probe] == num) {
                    // Found the element somewhere in the probe chain.
                    return true;
                } else if (dataArray[probe] == Integer.MIN_VALUE) {
                    // Hit an EMPTY slot: this breaks the probe chain.
                    // Because we never "skip" from a filled slot directly to EMPTY
                    // (except via DELETED), seeing EMPTY means the element cannot
                    // exist later in this chain.
                    //
                    // Note: we intentionally do NOT stop on DELETED.
                    return false;
                }
                // If it's DELETED or some other element, keep probing.
            }
        }
        // Scanned the whole table and never found 'num'.
        return false;
    }

    int baseHash(int elem, int capacity) {
        // Base hash: map an int to an array index in [0, capacity).
        // Simple version: modulo by capacity.
        int result = elem % capacity;
        if (result < 0) {
            // Java's % can be negative for negative numbers,
            // so normalize into the valid index range.
            return result + capacity;
        }
        return result;
    }

    int getSize() {
        // Number of logical elements in the set (ignores EMPTY and DELETED slots).
        return count;
    }

    @Override
    public String toString() {
        // Human-readable view of the set:
        // print all non-sentinel values along with the index they occupy.
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dataArray.length; i++) {
            if (dataArray[i] != Integer.MIN_VALUE && dataArray[i] != Integer.MIN_VALUE + 1) {
                sb.append(dataArray[i] + ":" + i + " ");
            }
        }
        return sb.toString();
    }

    void resize() {
        // When we resize:
        // - We double the array capacity.
        // - We re-insert all REAL elements into the new array using the new capacity.
        // - We DROP all tombstones, so the new table is clean (no DELETED slots).
        int newCapacity = capacity * 2;
        int newArray[] = new int[newCapacity];
        Arrays.fill(newArray, Integer.MIN_VALUE); // all slots start as EMPTY

        for (int i = 0; i < dataArray.length; i++) {
            int temp = dataArray[i];

            // Skip EMPTY and DELETED; only rehash actual elements.
            if (temp == Integer.MIN_VALUE || temp == Integer.MIN_VALUE + 1) {
                continue;
            }

            int newHash = baseHash(temp, newCapacity);

            // Standard linear probing in the new array.
            for (int x = 0; x < newCapacity; x++) {
                int probe = (newHash + x) % newCapacity;
                if (newArray[probe] == Integer.MIN_VALUE) {
                    newArray[probe] = temp;
                    break;
                }
            }
        }

        // Swap in the new storage.
        dataArray = newArray;
        capacity = newCapacity;
        // 'count' stays the same because we only moved real elements.
    }

    void printDataArray() {
        // Debugging helper: dump the raw array content,
        // including EMPTY and DELETED sentinel values.
        // This lets you see probe chains and tombstones as they really are.
        for (int i = 0; i < dataArray.length; i++) {
            System.out.print(dataArray[i] + ", ");
        }
        System.out.println();
    }

    void remove(int elem) { // Aakhri padaav: deletion with tombstones
        /*
            Deletion rules with open addressing:

            - We CANNOT simply replace a removed element with EMPTY, because that would
              "break" probe chains for elements that were inserted later.

              Example:
                  [ keyA, keyB, keyC ] in consecutive slots
                  All three collided and ended up next to each other.
                  If we remove keyB and set its slot to EMPTY:
                    Searching for keyC:
                        see keyA (not match)
                        see EMPTY where keyB was -> assume the cluster is over
                        -> we never check keyC's slot -> bug.

            - Instead we mark removed slots as DELETED (a tombstone).
              This tells 'contains' and 'add':
                "nothing is here right now, but the probe chain continues past me".
        */

        int hash = baseHash(elem, capacity);

        for (int i = 0; i < capacity; i++) {
            int probe = (hash + i) % capacity;

            if (dataArray[probe] == elem) {
                // Found the element, turn it into a tombstone.
                dataArray[probe] = Integer.MIN_VALUE + 1;
                this.count--;
                return;
            } else if (dataArray[probe] == Integer.MIN_VALUE) {
                // Hit an EMPTY slot before ever seeing 'elem' in this chain:
                // that means 'elem' is not present in the set.
                return;
            }
            // If it's DELETED or some other element, keep probing.
        }
    }
}

class Demo {
    public static void main(String[] args) {
        // Just using Java's HashSet to compare / sanity check in a basic way.
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
        mySet.add(48); // duplicate
        mySet.add(1);
        mySet.add(2);
        mySet.add(3);
        mySet.add(4);
        mySet.add(5);
        mySet.add(9);
        mySet.add(19);
        mySet.add(25); // should trigger resize around here

        System.out.println(mySet);
        System.out.println(mySet.contains(43)); // expected: true
        System.out.println(mySet.contains(79)); // expected: false
        System.out.println(mySet.contains(46)); // expected: true
        System.out.println(mySet.count);

        mySet.printDataArray();

        mySet.remove(46);
        System.out.println(mySet.count);
        System.out.println(mySet);
        mySet.printDataArray();

        // Re-insert 25 again to see how tombstones interact with add.
        mySet.add(25);
        mySet.printDataArray();
    }
}
