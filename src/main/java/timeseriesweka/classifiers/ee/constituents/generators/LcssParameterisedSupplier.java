package timeseriesweka.classifiers.ee.constituents.generators;

import timeseriesweka.classifiers.ee.constituents.*;
import timeseriesweka.classifiers.ee.index.IndexedSupplierObtainer;
import timeseriesweka.classifiers.ee.index.LinearInterpolater;
import timeseriesweka.measures.lcss.Lcss;
import utilities.StatisticUtilities;
import utilities.Utilities;
import weka.core.Instances;

import java.io.IOException;
import java.util.List;

public class LcssParameterisedSupplier extends ParameterisedSupplier<Lcss> {

    private final IndexedMutator<Lcss, Double> warpingWindowParameter = new IndexedMutator<>(Lcss.WARPING_WINDOW_MUTABLE);
    private final TargetedMutator<Lcss> warpingWindowMutator = new TargetedMutator<>(warpingWindowParameter, getBox());
    private final IndexedMutator<Lcss, Double> costParameter = new IndexedMutator<>(Lcss.COST_MUTABLE);
    private final TargetedMutator<Lcss> costMutator = new TargetedMutator<>(costParameter, getBox());

    public LcssParameterisedSupplier() {
        List<Indexed> parameters = getParameters().getIndexeds();
        parameters.add(costMutator);
        parameters.add(warpingWindowMutator);
    }

    @Override
    public void setParameterRanges(final Instances instances) {
        double pStdDev = StatisticUtilities.populationStandardDeviation(instances);
        int instanceLength = instances.numAttributes() - 1;
//        warpingWindowParameter.getValueRange().setIndexedSupplier(new LinearInterpolater(0, 0.25 * instanceLength, 10));
        warpingWindowParameter.getValueRange().setIndexedSupplier(new IndexedSupplierObtainer<Double>(10) {

            final int numAttrs = (int) (0.25 * instanceLength);
            final double diff = (double) (numAttrs) / (size() - 1);

            @Override
            protected Double obtain(final double value) {
                if(value == 1) {
                    return (double) numAttrs;
                } else {
                    return (double) ((int) (diff * ((int) (value * size()))));
                }
            }
        });
        costParameter.getValueRange().setIndexedSupplier(new LinearInterpolater(0.2 * pStdDev, pStdDev, 10));
    }

    @Override
    protected Lcss get() {
        return new Lcss();
    }

    public static void main(String[] args) throws IOException {
        LcssParameterisedSupplier lcssParameterisedSupplier = new LcssParameterisedSupplier();
        Instances instances = Utilities.loadDataset("/home/vte14wgu/TSCProblems2018/AllGestureWiimoteY");
        lcssParameterisedSupplier.setParameterRanges(instances);
        for(int i = 0; i < lcssParameterisedSupplier.size(); i++) {
            Lcss lcss = lcssParameterisedSupplier.get(i);
            System.out.println(lcss.getParameters());
        }
    }
}
