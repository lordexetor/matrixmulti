public class Worker{

    public void run(){
        stopped.set(false);
		worker.setIdentity(id.getBytes());
        worker.connect(serverBackendURL);
		// Tell server we're ready for work
		worker.send("READY");
        while(!stopped.get()){
            // Try to fetch a problem from the server
            PartialProblem partialProblem = getServerPartialproblem();
            // If the server supplied us a problem, solve it. Then null the problem.
            PartialSolution partialSolution;
            if(partialProblem != null){
                partialSolution = solveProblem(partialProblem)
                partialProblem = null;
            }
            // If there is a solved Problem, return value to server
            if(matrix != null){
                // TODO: sendValue(value)
                matrix = null;
            }
        }
    }


    /** Returns the result of A x B 
    TODO: Document and javascriptify
    */
    private PartialSolution multiplyMatrices(PartialProblem _partialProblem){
        // if (matrixA.getNumberOfColumns() == matrixB.getNumberOfRows()) {
        //     //  Solve the matrix.
        //     Matrix matrixC = new Matrix(matrixA.getNumberOfRows(), matrixB.getNumberOfColumns());
        //     for (row_A = 0; row_A < matrixA.getNumberOfRows(); row_A++) {
        //         row = martrixA.getRow(row_A);                        
        //         for (column_B = 0; column_B < matrixB.getNumberOfColumns(); column_B++) {
        //             sum = 0;
        //             column = B.getColumn(column_B);
        //             for (i = 0; i < row.length; i++) {
        //                 sum += row[i] * column[i];
        //             }
        //             C.setValue(row_A, column_B, sum);
        //         }
        //     }
        //     return C;
        // } else {
        //     throw new Exception("The matrices can not be multiplied.");
        // }
        return null;
    }

    /** Retrives a MatrixTask from the Server */
    private PartialProblem getServerPartialproblem(){
        while (!stopped.get()) {
			clientAddress = worker.recvStr();
			empty = worker.recvStr();
			assert (empty.length() == 0);
			data = worker.recv();
            PartialProblem partialProblem;// TODO: = serialize(data);
            if(matrixTask!=null) return partialProblem;
        }
        return null;
    }

    /** Uses a PartialProblem to get the row and column that should be computed.
    Using these Arrays, we can call the multiply Matrices function to solve the problem.
    After that, the partialSolution is returned.*/
    private PartialSolution solveProblem(_partialProblem){
        PartialSolution partialSolution = multiplyMatrices(_partialProblem);
        return result;
    }
}