package Linear;

import Functions.Evaluation;
import Functions.PreProcess;
import Functions.ReadFile;
import Jama.Matrix;

/**
 * Created by Zell on 12/14/15.
 */
public class AdditiveLinear extends Linear{

    public static void main(String[] args){
        int numberInput = 11;
        int numberOutput = 1;
        int numberTrainingRows = 1279;
        int numberTestRows = 320;

        AdditiveLinear ln = new AdditiveLinear();
        Matrix betaS = ln.trainingLinear(numberInput,numberOutput,numberTrainingRows);
        Matrix predict = ln.testLinear(numberInput,numberOutput,numberTestRows,betaS);

        /***Print predicted value ***/
        System.out.println("Predicted value: ");
        predict.print(1,1);
        /***End printing ***/

    }

    @Override
    public Matrix trainingLinear(int numberInput, int numberOutput, int numberRows) {
        ReadFile read = new ReadFile();
        PreProcess pre = new PreProcess();
        double[][] wineArray = read.runCSV("TrainingWineData.csv", numberRows, numberInput + numberOutput);

        int[] attritbutes = {0,1,2,3,4,5,6,7,8,9,10};
        wineArray = pre.normalize(wineArray, attritbutes);

        wineArray = pre.addictive(wineArray);
        numberInput = wineArray[1].length-1;

        //attritbutes = new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22};
        //wineArray = pre.clearOutlier(wineArray, numberInput + numberOutput, attritbutes);

        System.out.println(numberRows);
        numberRows = wineArray.length;

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

    @Override
    public Matrix testLinear(int numberInput, int numberOutput, int numberRows, Matrix betaS){
        ReadFile read = new ReadFile();
        PreProcess pre = new PreProcess();
        double[][] wineArray = read.runCSV("TestWineData.csv", numberRows, numberInput + numberOutput);

        int[] attritbutes = {0,1,2,3,4,5,6,7,8,9,10};
        wineArray = pre.normalize(wineArray, attritbutes);
        wineArray = pre.addictive(wineArray);

        numberInput = wineArray[1].length-1;

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
        /*** End printing ***/


        /***Print real values ***/
        System.out.println("Real value: ");
        Ys.print(1,1);
        System.out.println("------------------");
        /***End printing***/

        return predict.transpose();
    }
}
