package Functions;

import java.io.*;

/**
 * Created by khang on 05/11/2015.
 */
public class ReadFile {
    public double[][] runDat(String fileName, int instance, int attribute) {
        String csvFile = fileName;
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = "\\s+";
        int i = 0;
        int j = 0;
        String[][] salary = new String[instance][attribute+1];
        try {
            br = new BufferedReader(new FileReader(csvFile));
            br.readLine();
            while ((line = br.readLine()) != null) {
                //use comma as separator
                salary[i] = line.split(csvSplitBy);
                //System.out.println(i + ". " + salary[i][1] + "|");
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for ( i=0; i<instance; i++ ){
            if(salary[i][1].equals("male")){
                salary[i][1] = "1";
            }else{
                salary[i][1] = "0";
            }

            if(salary[i][2].equals("full")){
                salary[i][2] = "2";
            }else if(salary[i][2].equals("associate")){
                salary[i][2] = "1";
            }
            else salary[i][2] = "0";

            if(salary[i][4].equals("doctorate")){
                salary[i][4] = "1";
            }else salary[i][4] = "0";

        }

        double[][] salaryData = new double[instance][attribute];
        for (j = 1; j < (attribute+1); j++) {
            for (i = 0; i < instance; i++) {
                if (salary[i][j] != null) {
                    salaryData[i][j-1] = Double.parseDouble(salary[i][j]);
                }
            }
        }


        return salaryData;
    }

    public double[][] runCSV(String fileName, int instance, int attribute) {
        String csvFile = fileName;
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ";";
        int i = 0;
        int j = 0;
        String[][] wine = new String[instance][attribute];
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                //use comma as separator
                wine[i] = line.split(csvSplitBy);
                /*System.out.println(i+".");/Users/mac/Desktop/Screen Shot 2015-11-08 at 04.41.14.png
                for( j=0; j<12; j++ ) {
                    System.out.print(wine[i][j] + "|");
                }
                System.out.println("\n");*/
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        double[][] wineData = new double[instance][attribute];
        for ( j = 0; j < attribute; j++) {
            for (i = 0; i < instance; i++) {
                if (wine[i][j] != null) {
                    wineData[i][j] = Double.parseDouble(wine[i][j]);
                }
            }
        }
        /*for ( i = 0; i < instance; i++) {
            if (wineData[i][11] != 0.0)
                System.out.println(i + ". quality " + wineData[i][11]);
        }*/
        return wineData;
    }
}
