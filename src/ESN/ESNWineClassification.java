package ESN;

import Jama.Matrix;
import Functions.*;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Zell on 11/11/15.
 */
public class ESNWineClassification {
    public static void main(String[] args) {
        PreProcess obj = new PreProcess();
        ESNWineClassification esn = new ESNWineClassification();

        int numberInput = 11;
        int numberOutput = 1;
        int numberTrainingRows = 1279;
        int numberTestRows = 320;
        int numberHidden = 500;

        long start = System.currentTimeMillis();
        Matrix V = obj.generateRandomMatrix(numberInput, numberHidden);
        Matrix Wr = obj.generateRandomMatrix(numberHidden, numberHidden);

        Matrix wOut = esn.trainingLM(numberInput,numberOutput,numberTrainingRows, numberHidden, V, Wr);
        long end = System.currentTimeMillis();
        long total = end - start;

        esn.testLM(numberInput, numberOutput, numberTestRows, numberHidden, wOut, V, Wr);

        System.out.print("=====================\n"+"Runtime: "+ total + " ms" + "\n=====================");
    }

    public Matrix trainingLM(int numberInput, int numberOutput, int numberRows, int numberHidden, Matrix V, Matrix Wr){
        PreProcess pre = new PreProcess();
        ReadFile read = new ReadFile();
        double[][] initial_wineArray = read.runCSV("TrainingWineData.csv", numberRows, numberInput + numberOutput);

        int[] attritbutes = {0,1,2,3,4,5,6,7,8,9,10};
        double[][] wineArray = pre.normalize(initial_wineArray, attritbutes);
        //double[][] wineArray = pre.clearOutlier(initial2_wineArray, numberInput + numberOutput, attritbutes);

        numberRows = wineArray.length;
        double[][] new_wineArray = pre.addOne(wineArray);

        //Convert the array into the matrix
        Matrix wineData = new Matrix(new_wineArray);

        //Create submatrix X
        Matrix Xs = wineData.getMatrix(0, numberRows-1, 0, numberInput);

        //Create submatrix Y
        Matrix Ys = wineData.getMatrix(0, numberRows-1, numberInput+numberOutput,numberInput+numberOutput);

        double[][] temp_z_array = new double[numberRows][numberHidden];
        for(int i = 0; i < numberHidden; i++){
            temp_z_array[0][i] = 0;
        }

        for(int i = 1; i < numberRows; i++){
            Matrix subXs = Xs.getMatrix(i,i,1,numberInput);
            Matrix subXt = subXs.transpose();
            Matrix subZs1 =(V.transpose()).times(subXt);
            Matrix h_previous = new Matrix(temp_z_array[i-1],1);
            Matrix subZs2 = (Wr.transpose()).times(h_previous.transpose());

            Matrix subZs = subZs1.minus(subZs2);

            Matrix gSubZs = pre.sigmoidFunction(subZs);
            double[] subZs_array = gSubZs.getColumnPackedCopy();
            temp_z_array[i] = subZs_array;
        }

        for(int i = 0; i < numberHidden; i++){
            temp_z_array[0][i] = Math.random()*(pre.getMax(Arrays.copyOfRange(temp_z_array, 1, temp_z_array.length-1), i)
                    - pre.getMin(Arrays.copyOfRange(temp_z_array, 1, temp_z_array.length-1), i)) + pre.getMin(Arrays.copyOfRange(temp_z_array, 1, temp_z_array.length-1), i);
        }

        double[][] z_array = pre.addOne(temp_z_array);

        Matrix Z = new Matrix(z_array);

        Matrix subW1 = Z.times(Z.transpose());

        //System.out.print(subW1.getRowDimension() + "x" + subW1.getColumnDimension());
        Matrix id = null;
        id = id.identity(numberRows,numberRows);
        //Id.print(52,3);
        Matrix gamma_Id = id.times(0.001);
        subW1 = subW1.plus(gamma_Id);
        subW1 = subW1.inverse();
        Matrix wOut = (subW1.transpose()).times(Z);
        //System.out.print(wOut.getColumnDimension()+"x"+wOut.getRowDimension());
        wOut = (wOut.transpose()).times(Ys);
        //wOut.print(1,3);

        return wOut;
    }

    public void testLM(int numberInput, int numberOutput, int numberRows, int numberHidden, Matrix wOut, Matrix V, Matrix Wr){
        ReadFile read = new ReadFile();
        PreProcess pre = new PreProcess();
        double[][] initial_wineArray = read.runCSV("TestWineData.csv", numberRows, numberInput + numberOutput);

        int[] attritbutes = {0,1,2,3,4,5,6,7,8,9,10};
        double[][] wineArray = pre.normalize(initial_wineArray, attritbutes);
        //double[][] wineArray = dat.clearOutlier(initial2_wineArray, numberInput + numberOutput, attritbutes);


        //numberRows = wineArray.length;
        //test_wine.print(6,3);
        double[][] new_wineArray = pre.addOne(wineArray);

        //Convert the array into the matrix
        Matrix wineData = new Matrix(new_wineArray);

        //Create submatrix X
        Matrix Xs = wineData.getMatrix(0, numberRows-1, 0, numberInput);

        //Create submatrix Y
        Matrix Ys = wineData.getMatrix(0, numberRows-1, numberInput+numberOutput,numberInput+numberOutput);

        double[][] temp_z_array = new double[numberRows][numberHidden];
        for(int i = 0; i < numberHidden; i++){
            temp_z_array[0][i] = 0;
        }

        for(int i = 1; i < numberRows; i++){
            Matrix subXs = Xs.getMatrix(i,i,1,numberInput);
            Matrix subZs1 =(V.transpose()).times(subXs.transpose());
            Matrix h_previous = new Matrix(temp_z_array[i-1],1);
            Matrix subZs2 = (Wr.transpose()).times(h_previous.transpose());

            Matrix subZs = subZs1.minus(subZs2);

            Matrix gSubZs = pre.sigmoidFunction(subZs);
            double[] subZs_array = gSubZs.getColumnPackedCopy();
            temp_z_array[i] = subZs_array;
        }

        for(int i = 0; i < numberHidden; i++){
            temp_z_array[0][i] = Math.random()*(pre.getMax(Arrays.copyOfRange(temp_z_array, 1, temp_z_array.length-1), i)
            - pre.getMin(Arrays.copyOfRange(temp_z_array, 1, temp_z_array.length-1), i)) + pre.getMin(Arrays.copyOfRange(temp_z_array, 1, temp_z_array.length-1), i);
        }

        double[][] z_array = pre.addOne(temp_z_array);

        Matrix Z = new Matrix(z_array);

        double[][] Y_predicted_array = new double[numberRows][numberOutput];
        for(int i = 0; i < numberRows; i++){
            Matrix subZ = Z.getMatrix(i, i, 0, numberHidden);
            Matrix subY = (wOut.transpose()).times(subZ.transpose());
            double[] subY_array = subY.getColumnPackedCopy();
            Y_predicted_array[i] = subY_array;
        }

        Matrix Y_predicted = new Matrix(Y_predicted_array);

        /*** Mean squared Error ***/
        Evaluation evaluation = new Evaluation();
        double mse = evaluation.error(Ys, Y_predicted);
        System.out.println("-----------------------------------------");
        System.out.println("--Mean Square Error: " + mse + "--");
        System.out.println("-----------------------------------------" + "\n");
        /*** End prinitng ***/

        /*** Print Real values ***/
        System.out.println("Real value: ");
        Ys.print(1,2);
        System.out.println("-----------------");

        /*** Print predicted values ***/
        System.out.println("Predicted value: ");
        Y_predicted.print(1,2);

    }

}
