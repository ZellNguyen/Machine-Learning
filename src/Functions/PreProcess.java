package Functions;

import Jama.Matrix;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;

import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.exp;
import static java.lang.StrictMath.sqrt;

/**
 * Created by Zell on 12/9/15.
 */
public class PreProcess {
    public double[][] clearOutlier(double[][]  array, int numberAttribute, int[] attribute){
        PreProcess obj = new PreProcess();
        int countRows = 0;
        double[] checkDown = new double[array[0].length];
        double[] checkUp = new double[array[0].length];
        double[] mean = new double[array[0].length];
        double[] var = new double[array[0].length];
        for(int j = 0; j < attribute.length; j++){
            mean[attribute[j]] = obj.caclMean(array, attribute[j]);
            var[attribute[j]] = obj.caclVariance(array, attribute[j], mean[attribute[j]]);
            checkDown[attribute[j]] = mean[attribute[j]] - (6*sqrt(var[attribute[j]]));
            checkUp[attribute[j]] = mean[attribute[j]] + (6*sqrt(var[attribute[j]]));
            //System.out.println("Var: "+ var[attribute[j]]);
            //System.out.println("Mean: "+mean[attribute[j]]);
        }


        for(int j = 0; j < attribute.length; j++){
            int count = 0;

            //System.out.println(checkDown[attribute[j]] + ":" + checkUp[attribute[j]]);

            for(int i = 0; i < array.length; i++) {
                if (array[i][attribute[j]] < checkDown[attribute[j]] || array[i][attribute[j]] > checkUp[attribute[j]]) {
                    array[i][attribute[j]] = -1;
                    //System.out.print(array[i][j]);
                }
            }

        }

        for(int i = 0; i < array.length; i++){
            int count = 0;
            for(int j = 0; j < attribute.length; j++){
                if(array[i][attribute[j]]==-1) count++;
            }
            if(count!=0) countRows++;
        }

        double[][] new_array = new double[array.length-countRows][numberAttribute];
        for (int i1 = 0; i1 < new_array.length; i1++){
            innerloop:
            for(int i2 = 0; i2 < array.length; i2++){
                boolean isBreak = false;
                boolean delete = false;
                for(int j2=0; j2 < attribute.length; j2++){
                    if(array[i2][attribute[j2]]==-1) {
                        delete = true;
                    }

                }
                if(!delete){
                    for(int j2 = 0; j2 < numberAttribute; j2++){
                        new_array[i1][j2] = array[i2][j2];
                        array[i2][j2] = -1;
                    }
                    isBreak = true;
                }
                if(isBreak) break innerloop;
            }

        }

        return new_array;
    }

    public double[][] normalize(double[][] array, int[] attribute){
        PreProcess obj = new PreProcess();

        for(int j = 0; j < attribute.length; j++) {
            double max = obj.getMax(array, attribute[j]);
            double min = obj.getMin(array, attribute[j]);
            for(int i = 0; i < array.length; i++){
                array[i][attribute[j]] = max - array[i][attribute[j]];
                array[i][attribute[j]] = array[i][attribute[j]]/(max-min);
                array[i][attribute[j]] = 1 - array[i][attribute[j]];
            }
        }

        return array;
    }

    public double caclMean(double[][] array, int column_index) {
        double sum = 0;
        double mean = 0;
        int count = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i][column_index] != 0.0) {
                sum += array[i][column_index];
                count += 1;
            }
        }

        mean = sum / count;
        //System.out.println("Sum: " + sum + "; count: " + count + "; Mean = sum/count = " + mean);
        return mean;
    }

    public double caclVariance(double[][] array, int column_index, double mean) {
        double variance = 0;
        double temp = 0;
        int count = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i][column_index] != 0) {
                temp = array[i][column_index] - mean;
                temp *=  temp;
                variance += temp;
                count += 1;
            }
        }
        variance = variance/count;
        //System.out.println("Count: " + count + "; Variance: " + variance);
        return variance;
    }

    public double[][] addOne(double[][] array) {
        double[][] newArray = new double[array.length][];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = Arrays.copyOf(array[i], array[i].length + 1);
            for (int j = newArray[i].length - 1; j > 0; j--) {
                newArray[i][j] = array[i][j - 1];
            }
            newArray[i][0] = 1;

            //System.out.println(i + ". " + newArray[i][0] + "; " + newArray[i][1]);
        }
        return newArray;
    }

    public Matrix generateRandomMatrix(int column, int row){
        double[][] randomWeight = new double[column][row];
        for(int i = 0; i<column; i++){
            for(int j = 0; j<row; j++){
                double random = new Random().nextDouble();
                randomWeight[i][j] = random*0.4 - 0.2;
            }
        }
        PreProcess obj = new PreProcess();
        Matrix randomMatrix = new Matrix(randomWeight);
        /*Matrix mT = m.transpose();
        double[][] new_randomWeightT = mT.getArray();
        double[][] new_randomWeight = obj.addOne(new_randomWeightT);
        Matrix randomMatrix = new Matrix(new_randomWeight);*/
        //randomMatrix.print(column,3);
        return randomMatrix;
    }

    public Matrix sigmoidFunction(Matrix X){
        Matrix gX = X;
        for(int i = 0; i < X.getRowDimension(); i++){
            for(int j = 0; j < X.getColumnDimension(); j++){
                double sigmoid = 1/(1+exp(X.get(i,j)));
                gX.set(i,j,sigmoid);
            }
        }
        return gX;
    }

    public double getMax(double[][] array, int columnIndex){
        double max = array[0][columnIndex];
        for(int i = 0; i < array.length ; i++){
            if(array[i][columnIndex] > max){
                max = array[i][columnIndex];
            }
        }
        return max;
    }

    public double getMin(double[][] array, int columnIndex){
        double min = array[0][columnIndex];
        for(int i = 0; i < array.length ; i++){
            if(array[i][columnIndex] < min){
                min = array[i][columnIndex];
            }
        }
        return min;
    }

    public double[][] addictive(double[][] array){
        double[][] addArray = new double[array.length][array[0].length*2-1];

        for(int i = 0; i < array.length; i++){
            addArray[i] = Arrays.copyOf(array[i], array[i].length*2-1);
            for(int j = array[i].length-1; j < addArray[i].length-1; j++){
                addArray[i][j] = (array[i][j-array[i].length + 1])*(array[i][j-array[i].length + 1]);
            }
            addArray[i][addArray[i].length-1] = array[i][array[i].length-1];
        }
        return addArray;
    }

    public static double round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }
}
