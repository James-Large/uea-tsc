package timeseriesweka.classifiers.ee;

import timeseriesweka.classifiers.AdvancedClassifier;
import timeseriesweka.classifiers.ee.constituents.Constituent;
import timeseriesweka.classifiers.ee.iteration.AbstractIndexIterator;
import timeseriesweka.classifiers.ee.iteration.ElementIterator;
import timeseriesweka.classifiers.ee.iteration.RandomIndexIterator;
import timeseriesweka.classifiers.ee.selection.BestPerTypeSelector;
import timeseriesweka.classifiers.ee.selection.Selector;
import utilities.ClassifierResults;
import utilities.Utilities;
import utilities.instances.Folds;
import utilities.range.Range;
import weka.classifiers.Classifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class Ee implements AdvancedClassifier {
    private final List<Constituent> constituents = new ArrayList<>();
    private final List<Classifier> selectedCandidates = null;
    private Long seed = null;
    private Selector<Classifier> candidateSelector = new BestPerTypeSelector<>();
    private final ElementIterator<Constituent> constituentIterator = new ElementIterator<>(constituents);

    public void setConstituentIndexIterator(final AbstractIndexIterator constituentIndexIterator) {
        constituentIterator.setIndexIterator(constituentIndexIterator);
    }

    @Override
    public void buildClassifier(final Instances trainInstances) throws Exception {
        reset(trainInstances);
        Folds folds = foldInstances(trainInstances);
        while (constituentIterator.hasNext()) {
            Constituent constituent = constituentIterator.next();
            if (!constituent.hasNext()) {
                constituentIterator.remove();
            } else {
                Classifier candidateClassifier = constituent.next();
                ClassifierResults results = new ClassifierResults();
                for (int i = 0; i < folds.size(); i++) {
                    Instances trainFold = folds.getTrain(i);
                    Instances testFold = folds.getTest(i);
                    candidateClassifier.buildClassifier(trainFold);
//                    results.addAll(candidateClassifier.predict(testFold)); // todo adjust this to predict on 2d arr using modern weka instances class
                }
            }
        }
    }

    private void reset(Instances trainInstances) {
        for (Constituent constituent : constituents) {
            constituent.getIndexedSupplier().setParameterRanges(trainInstances);
            constituent.reset();
        }
        constituentIterator.reset();
    }

    private Folds foldInstances(Instances instances) {
        Folds.Builder foldsBuilder = new Folds.Builder(instances);
        if (seed != null) {
            foldsBuilder.setSeed(seed);
        }
        return foldsBuilder.build();
    }

    @Override
    public double classifyInstance(final Instance testInstance) throws Exception {
        return Utilities.maxIndex(distributionForInstance(testInstance));
    }

    @Override
    public double[] distributionForInstance(final Instance testInstance) throws Exception {
        return null;
    }

    @Override
    public Capabilities getCapabilities() {
        return null;
    }

    @Override
    public void setSeed(final long seed) {

    }

    @Override
    public ClassifierResults predict(Instances testFold) throws Exception {
        return null;
    }

    @Override
    public void setSavePath(String path) {

    }

    @Override
    public void copyFromSerObject(Object obj) throws Exception {

    }

    @Override
    public void setTimeLimit(long time) {

    }

    @Override
    public String getParameters() {
        return null;
    }
}
