package edu.rice.comp322;

import edu.rice.hj.api.HjProcedure;
import edu.rice.hj.api.HjSuspendingCallable;
import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.runtime.util.Pair;

import java.util.Arrays;
import java.util.Random;

import static edu.rice.hj.Module1.*;

/**
 * <p>
 * {@code ReciprocalArraySum} computes the sum of reciprocals of array elements with 2-way parallelism.
 * </p>
 * <p>
 * The goal of this example program is to create an array of n random int's, and compute the sum of their reciprocals in
 * two ways: 1) Sequentially in method seqArraySum() 2) In parallel using two tasks in method parArraySum() The
 * profitability of the parallelism depends on the size of the array and the overhead of async creation.
 * </p>
 * <p>
 * Your assignment is to use two-way parallelism in method parArraySum() to obtain a smaller execution time than
 * seqArraySum().  Note that execution times and the impact of parallelism will vary depending on the machine that you
 * run this program on, and other applications that may be executing on the machine.
 * </p>
 *
 * @author Vivek Sarkar (vsarkar@rice.edu)
 */
public class ReciprocalArraySum {
    /**
     * Sequentially compute the sum of the reciprocals of the array elements.
     *
     * @param inX the input array.
     * @throws SuspendableException to mark this method may potentially block.
     */
    protected static double seqArraySum(final double[] inX) throws SuspendableException {

        // Use this array to safely write to memory from the async
        double[] sum = {0};

        // Wrap this for loop in an async so its abstract metrics can be counted
        finish(() -> {
            async(() -> {
                // Compute the sum of the reciprocals of the array elements
                for (int i = 0; i < inX.length; i++) {
                    sum[0] += 1 / inX[i];
                    // Call doWork() here to keep track abstractly of how much work is being done
                    doWork(1);
                }
            });
        });

        // Return the contents of the sum array
        return sum[0];
    }

    /**
     * Compute the sum of the reciprocals of the array elements in parallel using two asyncs.
     *
     * @param inX the input array.
     * @throws SuspendableException to mark this method may potentially block.
     */
    protected static double parArraySum2Asyncs(final double[] inX) throws SuspendableException {

        // Use this array to safely write to memory from both asyncs
        double[] sum = {0, 0};

        finish(() -> {
            // TODO Write a parallel version of the seqArraySum code above using two asyncs
            // Remember to add the calls to doWork(1) as seen above to keep track of abstract metrics.
                // Compute the sum of the reciprocals of the array elements
            async(() -> {
                for (int i = 0; i < inX.length / 2; i++) {
                    sum[0] += 1 / inX[i];
                    // Call doWork() here to keep track abstractly of how much work is being done
                    doWork(1);
                    }
                });
            for (int i = inX.length / 2; i < inX.length; i++) {
                sum[1] += 1 / inX[i];
                doWork(1);
            }
        });

        // Return the grand total sum
        // TODO Remember to call doWork(1) for this addition
        doWork(1);
        return sum[0] + sum[1];
    }

    /**
     * Compute the sum of the reciprocals of the array elements in parallel using four asyncs.
     *
     * @param inX the input array.
     * @throws SuspendableException to mark this method may potentially block.
     */
    protected static double parArraySum4Asyncs(final double[] inX) throws SuspendableException {

        // TODO Create a version of parArraySum2Asyncs that uses 4 asyncs.
        // How do you want to split up the work among the 4 tasks? Equally? Is this the best way?

        // Return the grand total sum
        double[] sum = {0, 0};

        finish(() -> {
            // TODO Write a parallel version of the seqArraySum code above using two asyncs
            // Remember to add the calls to doWork(1) as seen above to keep track of abstract metrics.
            async(() -> {
                // Compute the sum of the reciprocals of the array elements
                double[] sub1 = {0, 0};
                finish(() -> {
                    async(() -> {
                        for (int i = 0; i < inX.length / 4; i++) {
                            sub1[0] += 1 / inX[i];
                            // Call doWork() here to keep track abstractly of how much work is being done
                            doWork(1);
                        }
                    });
                    for (int i = inX.length / 4; i < inX.length / 2; i++) {
                        sub1[1] += 1 / inX[i];
                        // Call doWork() here to keep track abstractly of how much work is being done
                        doWork(1);
                    }
                });
                double newSub = sub1[0] + sub1[1];
                sum[0] += newSub;
                });
            double[] sub2 = {0, 0};
            finish(() -> {
                async(() -> {
                    for (int i = inX.length / 2; i < 3 * inX.length / 4; i++) {
                        sub2[0] += 1 / inX[i];
                        // Call doWork() here to keep track abstractly of how much work is being done
                        doWork(1);
                    }
                });
                for (int i = 3 * inX.length / 4; i < inX.length; i++) {
                    sub2[1] += 1 / inX[i];
                    // Call doWork() here to keep track abstractly of how much work is being done
                    doWork(1);
                }
            });
            double newSub2 = sub2[0] + sub2[1];
            sum[1] += newSub2;
            });

        // Return the grand total sum
        // TODO Remember to call doWork(1) for this addition
        doWork(1);
        return sum[0] + sum[1];
    }

    /**
     * Compute the sum of the reciprocals of the array elements in parallel using eight asyncs.
     *
     * @param inX the input array.
     * @throws SuspendableException to mark this method may potentially block.
     */
    protected static double parArraySum8Asyncs(final double[] inX) throws SuspendableException {

        // TODO Create a version of parArraySum2Asyncs that uses 8 asyncs.
        // Do you really want to have to create 8 asyncs manually?
        // Is there a better way you could write this function?
        // Remember that copying and pasting code is generally discouraged.

        // Return the grand total sum
        double[] inX1 = Arrays.copyOfRange(inX, 0, inX.length / 2 - 1);
        double[] inX2 = Arrays.copyOfRange(inX, inX.length / 2, inX.length - 1);
        double sum1 = parArraySum4Asyncs(inX1);
        double sum2 = parArraySum4Asyncs(inX2);
        return sum1 + sum2;
    }
}
