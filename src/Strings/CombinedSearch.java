//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package Strings;

import Strings.Search;
import java.io.IOException;
import java.util.Scanner;

public class CombinedSearch {
    public CombinedSearch() {
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Article name: ");
        String articleName = sc.next();
        Search article1 = new Search(articleName);
        article1.searchAb();
    }
}
