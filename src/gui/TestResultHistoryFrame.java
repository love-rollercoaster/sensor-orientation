package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import algorithms.Algorithms;

import test.TestResult;

@SuppressWarnings("serial")
public class TestResultHistoryFrame extends JFrame {

    private JPanel contentPane;
    private JTextField logFilepathField = new JTextField();
    private DefaultListModel testResultHistory = new DefaultListModel();

    private JTextField shortestPathHopCountRatioField = new JTextField();
    private JTextField lengthOfRouteRatioAverageField = new JTextField();
    private JTextField networkDiameterRatioAverageField = new JTextField();

    public TestResultHistoryFrame() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        setTitle("Log Viewer");

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());

        JPanel bottomPanel = makeLogControlPanel();
        JList testResultHistoryList = new JList(testResultHistory);

        testResultHistory.addListDataListener(new ListDataListener() {

            @Override
            public void intervalAdded(ListDataEvent e) {
                computeAndSetAverages();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                // TODO Auto-generated method stub
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                // TODO Auto-generated method stub
            }
        });

        contentPane.add(testResultHistoryList, BorderLayout.CENTER);
        contentPane.add(bottomPanel, BorderLayout.PAGE_END);
        contentPane.add(makeTestResultAveragePanel(), BorderLayout.LINE_START);

        shortestPathHopCountRatioField.setEditable(false);
        lengthOfRouteRatioAverageField.setEditable(false);
        networkDiameterRatioAverageField.setEditable(false);

        setContentPane(contentPane);
    }


    public void computeAndSetAverages() {
        double shortestPathHopCountRatioSum = 0;
        double lengthOfRouteRatioSum = 0;
        double networkDiameterRatioSum = 0;
        int numberOfResults = testResultHistory.getSize();

        for (int i = 0; i < testResultHistory.getSize(); i++) {
            TestResult result = (TestResult) testResultHistory.getElementAt(i);

            shortestPathHopCountRatioSum += result.getShortestPathHopCountRatio();
            lengthOfRouteRatioSum += result.getLengthOfRouteRatio();
            networkDiameterRatioSum += result.getNetworkDiameterRatio();
        }

        double lengthOfRouteRatioAverage    =  lengthOfRouteRatioSum / numberOfResults;
        double shortestPathHopCountRatioAverage = shortestPathHopCountRatioSum / numberOfResults;
        double networkDiameterRatioAverage = networkDiameterRatioSum / numberOfResults;

        shortestPathHopCountRatioField.setText(Double.toString(shortestPathHopCountRatioAverage));
        lengthOfRouteRatioAverageField.setText(Double.toString(lengthOfRouteRatioAverage));
        networkDiameterRatioAverageField.setText(Double.toString(networkDiameterRatioAverage));
    }

    public void logAverages(PrintStream log) {
        log.println("--------------------------------------------");
        log.println("So far, the average of all test results are:");
        log.println(" Shortest Path Ratio: " + shortestPathHopCountRatioField.getText());
        log.println("  Route Length Ratio: " + lengthOfRouteRatioAverageField.getText());
        log.println("      Diameter Ratio: " + networkDiameterRatioAverageField.getText());
        log.println("--------------------------------------------");
    }

    public void addTestResult(TestResult result) {
        testResultHistory.addElement(result);
    }

    private JPanel makeTestResultAveragePanel() {
        JPanel panel = new JPanel();

        panel.setLayout(new GridLayout(0,1));
        panel.add(new JLabel("Shortest Path Hop Count Average: "));
        panel.add(shortestPathHopCountRatioField);

        panel.add(new JLabel("Length of Route Ratio Average: "));
        panel.add(lengthOfRouteRatioAverageField);

        panel.add(new JLabel("Network Diameter Ratio Average: "));
        panel.add(networkDiameterRatioAverageField);

        return panel;
    }

    private JPanel makeLogControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(makeLogFilepathPanel());
        panel.add(makeExportTestResultsButton());
        panel.setBorder(new EmptyBorder(5, 0, 0, 0));

        return panel;
    }

    private JPanel makeLogFilepathPanel() {
        JLabel logFilepathLabel = new JLabel("Log Filepath: ");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(logFilepathLabel);
        panel.add(logFilepathField);

        return panel;
    }

    private JButton makeExportTestResultsButton() {
        JButton button = new JButton("Export Test Results");

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        return button;
    }

}
