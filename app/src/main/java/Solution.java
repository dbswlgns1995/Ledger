import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Solution {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String input = scanner.nextLine();
        char[] input_arr = input.toCharArray();

        int[][] move = {{-2,-1},{-1,-2},{1,-2},{2,-1},{-2,1},{-1,2},{1,2},{2,1}};

        int count = 0;

        int dx, dy;


        for(int i=0;i<8;i++){
            dx = input_arr[0] - (int)'a' + 1 + move[i][0];
            dy = (int)input_arr[1] - 48 + move[i][1];

            System.out.println(dx + " " + dy);
            if(dx>=1 && dx<=8 && dy>=1 && dy<=8){
                count++;
            }
        }

        System.out.println(count);






    }

}
