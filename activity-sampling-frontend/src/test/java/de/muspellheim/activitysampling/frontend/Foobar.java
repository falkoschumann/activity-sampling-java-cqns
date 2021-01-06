package de.muspellheim.activitysampling.frontend;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class Foobar {
  @Test
  void foo() {
    var string = "[Foo, Bar] Lorem ipsum";
    System.out.println("String: " + string);
    var pattern = Pattern.compile("(\\[(.+)])?\\s*(.+)");
    var matcher = pattern.matcher(string);
    if (matcher.find()) {
      System.out.println("Match 0: " + matcher.group(0));
      System.out.println("Match 1: " + matcher.group(1));
      System.out.println("Match 2: " + matcher.group(2));
      System.out.println("Match 3: " + matcher.group(3));
    }
  }
}
