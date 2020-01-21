package edu.rice.comp322;

import edu.rice.hj.api.HjMetrics;
import edu.rice.hj.runtime.config.HjSystemProperty;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import java.util.Random;

import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.api.HjProcedure;
import edu.rice.hj.api.HjSuspendingCallable;
import static edu.rice.comp322.ReciprocalArraySum.*;
import static edu.rice.hj.Module0.launchHabaneroApp;
import static edu.rice.hj.Module0.abstractMetrics;
import static edu.rice.hj.Module1.*;

/**
 * Unit test for simple App.
 */
public class Lab1CorrectnessTest extends TestCase {
    private static final int DEFAULT_N = 1_000_000;

    protected static double[] initializeArray(final int n) {
        final double[] X = new double[n];
        final Random myRand = new Random(n);

        for (int i = 0; i < n; i++) {
            X[i] = myRand.nextInt(n);
            if (X[i] == 0.0) {
                i--;
            }
        }
        return X;
    }

    protected static TestResult doExecute(final String name,
                                      final HjSuspendingCallable<Double> body) throws SuspendableException {
        System.out.println("  *** Executing " + name);
        final Double[] result = {0.0};

        // finish-async for abstract metrics computation
        finish(() -> {
            async(() -> {
                result[0] = body.call();
            });
        });

        final HjMetrics actualMetrics = abstractMetrics();
        System.out.println("Abstract metrics:");
        System.out.println(" Total work = number of calls to doWork() = " + actualMetrics.totalWork());
        System.out.println(" Critical path length through asyncs = " + actualMetrics.criticalPathLength());
        System.out.println(" Ideal parallelism = total work / CPL = " + actualMetrics.idealParallelism());

        return new TestResult(result[0]);
    }

    public void testReciprocalParallelism2Asyncs() {
        try {
            System.out.println("\nReciprocalArraySumTest.testReciprocalParallelism2Asyncs() starts...\n");

            @SuppressWarnings("unchecked")
            final TestResult[] results = new TestResult[2];
            final double[] X = initializeArray(DEFAULT_N);
            HjSystemProperty.abstractMetrics.set(true);

            // sequential
            launchHabaneroApp(() -> {
                results[0] = doExecute("seqArraySum", () -> seqArraySum(X));
                System.out.println("* running sequential version (ie, 1 async) *");
            });

            try {
                // two asyncs
                launchHabaneroApp(() -> {
                    results[1] = doExecute("parArraySum2Asyncs", () -> parArraySum2Asyncs(X));
                    System.out.println("* running parallel version with 2 asyncs *");
                    printReciprocalStats(abstractMetrics(), results);
                });
            } catch (final RuntimeException re) {
                if (re.getCause() instanceof AssertionFailedError) {
                    throw (AssertionFailedError) re.getCause();
                } else {
                    throw re;
                }
            }

        } finally {
            System.out.println("\nReciprocalArraySumTest.testReciprocalParallelism2Asyncs() ends.\n");
        }
    }

    public void testReciprocalParallelism4Asyncs() {
        try {
            System.out.println("\nReciprocalArraySumTest.testReciprocalParallelism4Asyncs() starts...\n");

            @SuppressWarnings("unchecked")
            final TestResult[] results = new TestResult[2];
            final double[] X = initializeArray(DEFAULT_N);
            HjSystemProperty.abstractMetrics.set(true);

            // sequential
            launchHabaneroApp(() -> {
                results[0] = doExecute("seqArraySum", () -> seqArraySum(X));
                System.out.println("* running sequential version (ie, 1 async) *");
            });

            try {
                // four asyncs
                launchHabaneroApp(() -> {
                    results[1] = doExecute("parArraySum4Asyncs", () -> parArraySum4Asyncs(X));
                    System.out.println("* running parallel version with 4 asyncs *");
                    printReciprocalStats(abstractMetrics(), results);
                });
            } catch (final RuntimeException re) {
                if (re.getCause() instanceof AssertionFailedError) {
                    throw (AssertionFailedError) re.getCause();
                } else {
                    throw re;
                }
            }

        } finally {
            System.out.println("\nReciprocalArraySumTest.testReciprocalParallelism4Asyncs() ends.\n");
        }
    }

    public void testReciprocalParallelism8Asyncs() {
        try {
            System.out.println("\nReciprocalArraySumTest.testReciprocalParallelism8Asyncs() starts...\n");

            @SuppressWarnings("unchecked")
            final TestResult[] results = new TestResult[2];
            final double[] X = initializeArray(DEFAULT_N);
            HjSystemProperty.abstractMetrics.set(true);

            // sequential
            launchHabaneroApp(() -> {
                results[0] = doExecute("seqArraySum", () -> seqArraySum(X));
                System.out.println("* running sequential version (ie, 1 async) *");
            });

            try {
                // eight asyncs
                launchHabaneroApp(() -> {
                    results[1] = doExecute("parArraySum8Asyncs", () -> parArraySum8Asyncs(X));
                    System.out.println("* running parallel version with 8 asyncs *");
                    printReciprocalStats(abstractMetrics(), results);
                });
            } catch (final RuntimeException re) {
                if (re.getCause() instanceof AssertionFailedError) {
                    throw (AssertionFailedError) re.getCause();
                } else {
                    throw re;
                }
            }
        } finally {
            System.out.println("\nReciprocalArraySumTest.testReciprocalParallelism8Asyncs() ends.\n");
        }
    }

    private void printReciprocalStats(final HjMetrics actualMetrics,
            final TestResult[] results) {
        // ensure the parallel code is faster than the sequential code
        assertTrue("Not achieving any ideal parallelism", actualMetrics.idealParallelism() > 1.1);

        // print actual metrics
        System.out.println("actual speedup metrics:");
        final TestResult seqRes = results[0];
        final TestResult parRes = results[1];

        // check for correctness
        final String seqResult = String.format("%8.5f", seqRes.result);
        final String parResult = String.format("%8.5f", parRes.result);
        assertTrue("Expected = " + seqResult + ", actual = " + parResult, parResult.compareTo(seqResult) == 0);
    }

    private static class TestResult {
        public final double result;

        public TestResult(final double result) {
            this.result = result;
        }
    }
}
