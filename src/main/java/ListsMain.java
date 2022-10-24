import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListsMain {
    public static void main(String[] args) {
        List<String> pets = new ArrayList<>();
        List<String> other1 = new ArrayList<>();
        List<String> other2 = new ArrayList<>();
        pets.add("cat");
        pets.add("canary");
        pets.add("dog");
        pets.add("fish");
        other1.add("gerbil");
        other1.add("iguana");
        other1.add("fish");
        other2.add("kangaroo");
        other2.add("gerbil");
        other2.add("fish");

        pets.remove(2);
        System.out.println("pets = " + pets);

        pets.removeIf(p -> p.startsWith("c"));
        System.out.println("pets = " + pets);

        pets.clear();
        System.out.println("pets = " + pets);

        pets.add("kangaroo");
        pets.add(0, "koala");
        System.out.println("pets = " + pets);

        pets.addAll(other1);
        pets.addAll(1, other1);
        System.out.println("pets = " + pets);

        pets.removeAll(other2);
        pets.replaceAll(String::toUpperCase);
        pets.set(1, "zebra");
        pets.remove("fish");
        System.out.println("pets = " + pets);

        pets.sort(Comparator.naturalOrder());
        System.out.println("pets = " + pets);

        pets.retainAll(other1);
        System.out.println("pets = " + pets);
    }
}
