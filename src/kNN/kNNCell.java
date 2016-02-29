package kNN;

import Jama.Matrix;
import Functions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Zell on 12/9/15.
 */
public class kNNCell extends kNN{

    public static void main(String[] args){
        kNNCell obj = new kNNCell();
        int numberTrainingRows = 1279;
        int numberTestRows = 320;
        int numberAtt = 12;
        int division = 3;
        long start = System.currentTimeMillis();
        double[][] training = obj.trainingkNN(numberAtt, numberTrainingRows);

        double[] predict = obj.testkNN(training, numberAtt, numberTestRows, division);
        long end = System.currentTimeMillis();
        long total = end - start;

        /*** Print predicted values ***/
        System.out.println("Predicted value: ");
        for(double data: predict){
            System.out.println(data);
        }
        System.out.println("-----------------");
        /*** End printing ***/
        System.out.print("=====================\n"+"Runtime: "+ total + " ms" + "\n=====================");
    }

    public double[] testkNN(double[][] training, int numberCol, int numberRow, int division){
        ReadFile read = new ReadFile();

        /*** Get test data ***/
        double[][] wine = read.runCSV("TestWineData.csv",numberRow, numberCol);

        Matrix wineX = new Matrix(wine, wine.length, wine[1].length-1);
        Matrix trainingX = new Matrix(training, training.length, training[1].length-1);
        //trainingX.print(1,1);

        double[][] wineX_array = wineX.getArray();
        double[][] trainingX_array = trainingX.getArray();

        /*** Start pattern recognition ***/
        double[] predict = new double[wine.length];
        int i = 0;

        for(double[] input: wineX_array){
            kNNCell obj = new kNNCell();
            int[] coordinateInput = obj.getCoordinate(input, trainingX_array, division);
            predict[i] = obj.majority(coordinateInput, training, division);
            i++;
        }

        Matrix wineData = new Matrix(wine);
        Matrix Ys = wineData.getMatrix(0, numberRow-1, numberCol-1,numberCol-1);

        /*** Mean squared Error ***/
        Matrix predicts = new Matrix(predict, 1);
        Evaluation evaluation = new Evaluation();
        double mse = evaluation.error(Ys, predicts.transpose());
        System.out.println("-----------------------------------------");
        System.out.println("--Mean Square Error: " + mse + "--");
        System.out.println("-----------------------------------------" + "\n");
        /*** End prinitng ***/

        /*** Print the real values ***/
        System.out.println("Real Value:");
        Ys.print(1,1);
        System.out.println("--------------------");
        /*** End printing ***/

        return predict;
    }

    public double getMax(double[][] array, int columnIndex){
        double max = array[columnIndex][0];
        for(int i = 0; i < array.length ; i++){
            if(array[i][columnIndex] > max){
                max = array[i][columnIndex];
            }
        }
        return max;
    }

    public double getMin(double[][] array, int columnIndex){
        double min = array[columnIndex][0];
        for(int i = 0; i < array.length ; i++){
            if(array[i][columnIndex] < min){
                min = array[i][columnIndex];
            }
        }
        return min;
    }

    public int[] getCoordinate(double[] input, double[][] array, int division){
        int j = 0;
        double[] max = new double[array[1].length];
        double[] min = new double[array[1].length];
        double[] range = new double[array[1].length];
        int[] coordinate = new int[array[1].length];
        for( j = 0; j < array[1].length; j++ ){
            max[j] = getMax(array, j);
            min[j] = getMin(array, j);
            range[j] = (max[j] - min[j])/(double)division;
        }

        for( j = 0; j < array[1].length; j++ ){
            coordinate[j] = (int) ((input[j] - min[j])/range[j]);
        }

        return coordinate;

    }

    public double majority(int[] cell, double[][] array, int division){
        int[][] cells = new int[array.length][array[0].length];
        int count = 0;
        double average = 0;

        for(int i = 0; i < array.length; i++) {
            cells[i] = getCoordinate(array[i], array, division);
            if(Arrays.equals(cells[i], cell)){
                average += array[i][array[i].length-1];
                count++;
            }
        }

        PreProcess pre = new PreProcess();

        if(count != 0 ){

            average = pre.round(average / count, 1);

            return average;
        }

        return pre.round(Math.random()*9 + 1, 1);
    }
}
