package ELM;

import Jama.Matrix;
import Functions.*;

/**
 * Created by Zell on 12/13/15.
 */
public class ELM {
    public static void main(String[] args){
        PreProcess pre = new PreProcess();

        int numberInput = 11;
        int numberOutput = 1;
        int numberTrainingRows = 1279;
        int numberTestRows = 320;
        int numberHidden = 500;


        ELM elm = new ELM();
        Matrix V = pre.generateRandomMatrix(numberInput, numberHidden);
        Matrix W = elm.trainingELM(numberInput,numberOutput,numberTrainingRows, V);


        long start = System.currentTimeMillis();
        Matrix Yp = elm.testELM(numberInput,numberOutput,numberTestRows,V,W);
        long end = System.currentTimeMillis();
        long total = end - start;
        System.out.println("Predicted value: ");
        Yp.print(1,2);

        System.out.print("=====================\n"+"Runtime: "+ total + " ms" + "\n=====================");
    }
    public Matrix trainingELM(int numberInput, int numberOutput, int numberRows, Matrix V){
        ReadFile read = new ReadFile();
        PreProcess pre = new PreProcess();
        double[][] wineArray = read.runCSV("TrainingWineData.csv", numberRows, numberInput + numberOutput);

        int[] attritbutes = {0,1,2,3,4,5,6,7,8,9,10};
        wineArray = pre.normalize(wineArray, attritbutes);
        //wineArray = pre.clearOutlier(wineArray, numberInput + numberOutput, attritbutes);

        numberRows = wineArray.length;

        //Convert the array into the matrix
        Matrix wineData = new Matrix(wineArray);

        //Create submatrix X
        Matrix Xs = wineData.getMatrix(0, numberRows-1, 0, numberInput-1);

        //Create submatrix Y
        Matrix Ys = wineData.getMatrix(0, numberRows-1, numberInput,numberInput);

        Matrix Z = (V.transpose()).times(Xs.transpose());
        Z = pre.sigmoidFunction(Z.transpose());
        Z = new Matrix(pre.addOne(Z.getArray()));

        Matrix id = null;
        id = id.identity(numberRows, numberRows);

        Matrix W = Z.times(Z.transpose());

        W = W.plus(id);
        W = W.inverse();
        W = W.times(Z);
        W = (W.transpose()).times(Ys);

        return W;
    }

    public Matrix testELM(int numberInput, int numberOutput, int numberRows, Matrix V, Matrix W){
        ReadFile read = new ReadFile();
        PreProcess pre = new PreProcess();
        double[][] initial_wineArray = read.runCSV("TestWineData.csv", numberRows, numberInput + numberOutput);

        int[] attritbutes = {0,1,2,3,4,5,6,7,8,9,10};
        double[][] wineArray = pre.normalize(initial_wineArray, attritbutes);

        Matrix wineData = new Matrix(wineArray);

        //Create submatrix X
        Matrix Xs = wineData.getMatrix(0, numberRows-1, 0, numberInput-1);

        Matrix Z = (V.transpose()).times(Xs.transpose());
        Z = pre.sigmoidFunction(Z.transpose());
        Z = new Matrix(pre.addOne(Z.getArray()));

        Matrix Yp = (W.transpose()).times(Z.transpose());
        Matrix Ys = wineData.getMatrix(0, numberRows-1, numberInput,numberInput);

        /*** Mean squared Error ***/
        Evaluation evaluation = new Evaluation();
        double mse = evaluation.error(Ys, Yp.transpose());
        System.out.println("-----------------------------------------");
        System.out.println("--Mean Square Error: " + mse + "--");
        System.out.println("-----------------------------------------" + "\n");
        /*** End prinitng ***/

        System.out.println("Real value: ");
        Ys.print(1,2);
        System.out.println("-------------");

        //return new Matrix(Y_predicted_array);
        return Yp.transpose();
    }
}
