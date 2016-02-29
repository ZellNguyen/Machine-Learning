package kNN;

import Functions.*;
import Jama.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Zell on 12/13/15.
 */
public class kNN {

    public static void main(String[] args){
        kNN obj = new kNN();
        int numberTrainingRows = 1279;
        int numberTestRows = 320;
        int numberAtt = 12;
        int k = 3;

        long start = System.currentTimeMillis();

        double[][] training = obj.trainingkNN(numberAtt, numberTrainingRows);

        double[] predict = obj.testkNN(training, numberAtt, numberTestRows, k);

        long end = System.currentTimeMillis();
        long total = end - start;

        /*** Print predicted values ***/
        System.out.println("Predicted value:");
        for(double data: predict){
            System.out.println(data);
        }
        System.out.println("----------------------");
        /*** End printing ***/
        System.out.print("=====================\n"+"Runtime: "+ total + " ms" + "\n=====================");
    }

    public double[][] trainingkNN(int numberCol, int numberRow){
        ReadFile read = new ReadFile();
        double[][] wine = read.runCSV("TrainingWineData.csv", numberRow, numberCol);

        /*** PreProcess.PreProcess Data ***/
        PreProcess pre = new PreProcess();
        int[] attritbutes = {0,1,2,3,4,5,6,7,8,9,10};
        //wine = pre.normalize(wine, attritbutes);
        //wine = pre.clearOutlier(wine, numberCol, attritbutes);
        System.out.println(wine.length);
        /*** End PreProcess.PreProcess ***/

        return wine;
    }

    public double[] testkNN(double[][] training, int numberCol, int numberRow, int k){
        ReadFile read = new ReadFile();

        /*** Get test data ***/
        double[][] test = read.runCSV("TestWineData.csv",numberRow, numberCol);

        /*** For each input point, get the kth nearest points ***/
        /*** k is the number of selected nearest points ***/
        double[] predict = new double[test.length];
        int i = 0;
        for(double[] input: test){
            double[] nearest = this.nearestPoint(input, training, k);
            predict[i] = this.getMajority(nearest);
            i++;
        }

        Matrix wineData = new Matrix(test);
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

    public Point[] distance(double[] point, double[][] training){
        Point[] points = new Point[training.length];

        for(int i = 0; i < training.length; i++){
            double distance = 0;
            for(int j = 0; j < point.length; j++){
                distance += (point[j] - training[i][j])*(point[j] - training[i][j]);
            }
            distance = Math.sqrt(distance);
            points[i] = new Point();
            points[i].setDistance(distance);
            points[i].setValue(training[i][training[i].length-1]);
        }

        return points;
    }

    public double[] nearestPoint(double[] point, double[][] training, int k){
        Point[] distance = this.distance(point, training);
        Selection sl = new Selection();
        Point[] nearestPoint = sl.select(distance, k);

        double[] nearest = new double[k];
        int i = 0;
        for(Point p: nearestPoint){
            nearest[i] = p.getValue();
            i++;
        }

        return nearest;
    }

    public double getMajority(double[] nearest){

        double average = 0;
        for(double value: nearest){
            average += value;
        }

        PreProcess pre = new PreProcess();
        average = pre.round(average/nearest.length, 1);

        return average;

    }
}
