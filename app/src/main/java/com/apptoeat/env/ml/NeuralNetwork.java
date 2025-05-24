package com.apptoeat.env.ml;

public class NeuralNetwork {
    private int inputNodes;
    private int hiddenNodes;
    private int outputNodes;
    private double[][] weightsInputHidden;
    private double[][] weightsHiddenOutput;

    public NeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes) {
        this.inputNodes = inputNodes;
        this.hiddenNodes = hiddenNodes;
        this.outputNodes = outputNodes;

        // Initialize weights randomly (between -1 and 1)
        weightsInputHidden = new double[inputNodes][hiddenNodes];
        weightsHiddenOutput = new double[hiddenNodes][outputNodes];
        randomizeMatrix(weightsInputHidden);
        randomizeMatrix(weightsHiddenOutput);
    }

    private void randomizeMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = Math.random() * 2 - 1;
            }
        }
    }

    // Activation function (sigmoid)
    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    // Feed-forward method: input -> hidden -> output
    public double[] feedForward(double[] inputs) {
        if (inputs.length != inputNodes) {
            throw new IllegalArgumentException("Input array length does not match input nodes.");
        }

        // Compute hidden layer activations
        double[] hidden = new double[hiddenNodes];
        for (int i = 0; i < hiddenNodes; i++) {
            double sum = 0;
            for (int j = 0; j < inputNodes; j++) {
                sum += inputs[j] * weightsInputHidden[j][i];
            }
            hidden[i] = sigmoid(sum);
        }

        // Compute output layer activations
        double[] outputs = new double[outputNodes];
        for (int i = 0; i < outputNodes; i++) {
            double sum = 0;
            for (int j = 0; j < hiddenNodes; j++) {
                sum += hidden[j] * weightsHiddenOutput[j][i];
            }
            outputs[i] = sigmoid(sum);
        }

        return outputs;
    }

    // Methods for mutation and crossover for evolution
    public NeuralNetwork mutate(double mutationRate) {
        // For weightsInputHidden
        for (int i = 0; i < weightsInputHidden.length; i++) {
            for (int j = 0; j < weightsInputHidden[i].length; j++) {
                if (Math.random() < mutationRate) {
                    weightsInputHidden[i][j] += (Math.random() * 2 - 1) * 0.1;
                }
            }
        }
        // For weightsHiddenOutput
        for (int i = 0; i < weightsHiddenOutput.length; i++) {
            for (int j = 0; j < weightsHiddenOutput[i].length; j++) {
                if (Math.random() < mutationRate) {
                    weightsHiddenOutput[i][j] += (Math.random() * 2 - 1) * 0.1;
                }
            }
        }

        return this;
    }
    @Override
    public NeuralNetwork clone() {
        NeuralNetwork cloned = new NeuralNetwork(inputNodes, hiddenNodes, outputNodes);

        // Deep copy weightsInputHidden
        for (int i = 0; i < inputNodes; i++) {
            for (int j = 0; j < hiddenNodes; j++) {
                cloned.weightsInputHidden[i][j] = this.weightsInputHidden[i][j];
            }
        }

        // Deep copy weightsHiddenOutput
        for (int i = 0; i < hiddenNodes; i++) {
            for (int j = 0; j < outputNodes; j++) {
                cloned.weightsHiddenOutput[i][j] = this.weightsHiddenOutput[i][j];
            }
        }

        return cloned;
    }

}

