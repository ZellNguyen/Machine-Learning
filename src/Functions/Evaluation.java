package Functions;

import Jama.Matrix;

/**
 * Created by Zell on 12/14/15.
 */
public class Evaluation {
    public double error(Matrix real, Matrix predict){
        int numberRows = real.getRowDimension();
        System.out.println(numberRows);
        double error;
        double sum_square_error = 0;
        for(int i=0; i<numberRows; i++){
            error = real.get(i, 0) - predict.get(i, 0);
            error *= error;
            sum_square_error += error;
        }

        double mse = sum_square_error/numberRows;
        return mse;
    }
}
