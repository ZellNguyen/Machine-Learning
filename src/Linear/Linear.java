package Linear;

import Functions.Evaluation;
import Jama.Matrix;
import Functions.PreProcess;
import Functions.ReadFile;

/**
 * Created by Zell on 12/13/15.
 */
public class Linear {

    public static void main(String[] args){
        int numberInput = 11;
        int numberOutput = 1;
        int numberTrainingRows = 1279;
        int numberTestRows = 320;

        Linear ln = new Linear();
        long start = System.currentTimeMillis();
        Matrix betaS = ln.trainingLinear(numberInput,numberOutput,numberTrainingRows);
        Matrix predict = ln.testLinear(numberInput,numberOutput,numberTestRows,betaS);
        long end = System.currentTimeMillis();
        long total = end - start;

        /***Print predicted value ***/
        System.out.println("Predicted value: ");
        predict.print(1,1);
        /***End printing ***/

        System.out.print("=====================\n"+"Runtime: "+ total + " ms" + "\n=====================");

    }

    public Matrix trainingLinear(int numberInput, int numberOutput, int numberRows){
        ReadFile read = new ReadFile();
        PreProcess pre = new PreProcess();
        double[][] initial_wineArray = read.runCSV("TrainingWineData.csv", numberRows, numberInput + numberOutput);

        int[] attritbutes = {0,1,2,3,4,5,6,7,8,9,10};
        double[][] wineArray = pre.normalize(initial_wineArray, attritbutes);
        //wineArray = pre.clearOutlier(wineArray, numberInput + numberOutput, attritbutes);

        numberRows = wineArray.length;
        System.out.println(numberRows);

        //Convert the array into the matrix
        Matrix wineData = new Matrix(wineArray);

        //Create submatrix X
        Matrix Xs = wineData.getMatrix(0, numberRows-1, 0, numberInput-1);

        //Create submatrix Y
        Matrix Ys = wineData.getMatrix(0, numberRows-1, numberInput,numberInput);

        //Compute beta
        Matrix transpose_Xs = Xs.transpose();
        Matrix temps = (transpose_Xs.times(Xs)).inverse();
        Matrix betaS = (temps.times(transpose_Xs)).times(Ys);

        return betaS;
    }

    public Matrix testLinear(int numberInput, int numberOutput, int numberRows, Matrix betaS){
        ReadFile read = new ReadFile();
        PreProcess pre = new PreProcess();
        double[][] wineArray = read.runCSV("TestWineData.csv", numberRows, numberInput + numberOutput);

        int[] attritbutes = {0,1,2,3,4,5,6,7,8,9,10};
        wineArray = pre.normalize(wineArray, attritbutes);

        Matrix wineData = new Matrix(wineArray);

        //Create matrices X, Y
        Matrix Xs = wineData.getMatrix(0, numberRows-1, 0, numberInput-1);
        Matrix Ys = wineData.getMatrix(0, numberRows-1, numberInput,numberInput);

        Matrix predict = (betaS.transpose()).times(Xs.transpose());

        /*** Mean squared Error ***/
        Evaluation evaluation = new Evaluation();
        double mse = evaluation.error(Ys, predict.transpose());
        System.out.println("-----------------------------------------");
        System.out.println("--Mean Square Error: " + mse + "--");
        System.out.println("-----------------------------------------" + "\n");
        /*** End prinitng ***/


        /***Print real values ***/
        System.out.println("Real value: ");
        Ys.print(1,1);
        System.out.println("------------------");
        /***End printing***/

        return predict.transpose();
    }

}
