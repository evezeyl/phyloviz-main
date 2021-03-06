/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package net.phyloviz.project.action;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.DistanceProvider;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.DataModel;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.DataSetTracker;
import net.phyloviz.core.data.Population;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.core.util.PopulationFactory;
import net.phyloviz.core.util.TypingFactory;
import net.phyloviz.project.ProjectItem;
import net.phyloviz.project.ProjectItemFactory;
import net.phyloviz.project.ProjectTypingDataFactory;
import net.phyloviz.upgmanjcore.visualization.PersistentVisualization;
import net.phyloviz.upgmanjcore.visualization.Visualization;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.netbeans.api.project.Project;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.windows.WindowManager;

public final class LoadDataSetAction extends AbstractAction {

    private static final String VIZ_FOLDER = "visualization";

    public LoadDataSetAction() {
        putValue(Action.NAME, "Load DataSet");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        final JDialog dialog = createLoadingDialog();
        
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                Properties prop = new Properties();
                String propFileName = "config.properties.pviz";

                String projectDir = getProjectLocation();
                File visualization = new File(projectDir, VIZ_FOLDER);
                try {
                    InputStream inputStream = new FileInputStream(new File(projectDir, propFileName));
                    prop.load(inputStream);

                    StatusDisplayer.getDefault().setStatusText("Loading DataSet...");

                    String dataSetName = prop.getProperty("dataset-name");
                    if (dataSetAlreadyOpened(dataSetName)) {
                        JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "DataSet already opened!");
                        WindowManager.getDefault().findTopComponent("DataSetExplorerTopComponent").requestActive();

                    } else {

                        String typingFactory = prop.getProperty("typing-factory"),
                                typingFile = prop.getProperty("typing-file"),
                                populationFactory = prop.getProperty("population-factory"),
                                populationFile = prop.getProperty("population-file"),
                                populationFK = prop.getProperty("population-foreign-key"),
                                algorithmOutput = prop.getProperty("algorithm-output"),
                                algorithmOutputFactory = prop.getProperty("algorithm-output-factory"),
                                algorithmOutputDistanceProvider = prop.getProperty("algorithm-output-distance"),
                                algorithmOutputLevel = prop.getProperty("algorithm-output-level"),
                                visualizations = prop.getProperty("visualization"),
                                vizfilter = prop.getProperty("visualization-filter"),
                                vizpalette = prop.getProperty("visualization-palette");

                        String[] algoOutput = algorithmOutput != null
                                ? algorithmOutput.split(",")
                                : new String[]{""};
                        String[] algoOutputFactory = algorithmOutputFactory != null
                                ? algorithmOutputFactory.split(",")
                                : new String[]{""};
                        String[] algoOutputDistance = algorithmOutputDistanceProvider != null
                                ? algorithmOutputDistanceProvider.split(",")
                                : new String[]{""};
                        String[] algoOutputLevel = algorithmOutputLevel != null
                                ? algorithmOutputLevel.split(",")
                                : new String[]{""};
                        String[] viz = visualizations != null
                                ? visualizations.split(",")
                                : new String[]{};

                        if (dataSetName != null && (!dataSetName.equals(""))) {

                            DataSet ds = new DataSet(dataSetName);

                            StatusDisplayer.getDefault().setStatusText("Loading typing data...");

                            TypingFactory tf = null;
                            TypingData<? extends AbstractProfile> td = null;

                            Collection<? extends ProjectTypingDataFactory> tfLookup = (Lookup.getDefault().lookupAll(ProjectTypingDataFactory.class));
                            for (ProjectTypingDataFactory ptdi : tfLookup) {
                                if (ptdi.getClass().getName().equals(typingFactory)) {
                                    tf = (TypingFactory) ptdi;
                                    td = ptdi.onLoad(new FileReader(new File(projectDir, typingFile)));
                                    td.setDescription(tf.toString());
                                    ds.add(ptdi);
                                }
                            }

                            Population pop = null;
                            if (populationFile != null && (!populationFile.equals("")) && populationFK != null) {

                                StatusDisplayer.getDefault().setStatusText("Loading isolate data...");
                                pop = ((PopulationFactory) Class.forName(populationFactory).newInstance())
                                        .loadPopulation(new FileReader(new File(projectDir, populationFile)));
                                ds.add(pop);

                                int key = Integer.parseInt(populationFK);
                                StatusDisplayer.getDefault().setStatusText("Integrating data...");
                                td = tf.integrateData(td, pop, key);
                                ds.setPopKey(key);
                            }

                            int v_i = 0;
                            Collection<? extends ProjectItemFactory> pifactory = (Lookup.getDefault().lookupAll(ProjectItemFactory.class));
                            for (int i = 0; i < algoOutput.length; i++) {

                                for (ProjectItemFactory pif : pifactory) {
                                    String pifName = pif.getClass().getName();
                                    if (pifName.equals(algoOutputFactory[i])) {

                                        Visualization v = new Visualization();
                                        PersistentVisualization pv = null;
                                        if (viz.length > v_i && viz[v_i].split("\\.")[0].equals(algoOutput[i].split("\\.")[2])) {
                                            try (FileInputStream fileIn = new FileInputStream(new File(visualization, viz[v_i++]))) {

                                                try (ObjectInputStream in = new ObjectInputStream(fileIn)) {

                                                    pv = (PersistentVisualization) in.readObject();

                                                }

                                            } catch (IOException | ClassNotFoundException e) {
                                                Exceptions.printStackTrace(e);
                                            }
                                            v.pv = pv;
                                            if (vizfilter != null) {
                                                TreeFilter treefilter = loadTreeFilter(visualization, vizfilter);
                                                v.filter = treefilter.filter;
                                                DataModel dm = treefilter.datamodel.equals(TypingData.class.getCanonicalName()) ? td : pop;
                                                v.category = loadCategoryPalette(visualization, vizpalette, dm, v.filter);
                                            }
                                        }
                                        StatusDisplayer.getDefault().setStatusText("Loading algorithms...");
                                        AbstractDistance ad = null;
                                        Collection<? extends DistanceProvider> dpLookup = (Lookup.getDefault().lookupAll(DistanceProvider.class));
                                        for (DistanceProvider dp : dpLookup) {
                                            if (dp.getClass().getCanonicalName().equals(algoOutputDistance[i])) {
                                                ad = dp.getDistance(td);
                                            }
                                        }

                                        ProjectItem pi = pif.loadData(ds, td, projectDir, algoOutput[i], ad, Integer.parseInt(algoOutputLevel[i]));
                                        if (pi != null) {
                                            pi.addVisualization(v);
                                            td.add(pi);
                                        } else {
                                            return;
                                        }
                                    }
                                }
                            }

                            ds.add(td);

                            Lookup.getDefault().lookup(DataSetTracker.class).add(ds);
                            StatusDisplayer.getDefault().setStatusText("Dataset loaded.");
                            WindowManager.getDefault().findTopComponent("DataSetExplorerTopComponent").requestActive();

                        } else {
                            Exceptions.printStackTrace(new IllegalArgumentException("DataSet name not found!"));
                        }
                    }
                } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | NumberFormatException e) {
                    Exceptions.printStackTrace(e);
                }
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    private String getProjectLocation() {

        Lookup lookup = Utilities.actionsGlobalContext();
        Project project = lookup.lookup(Project.class
        );
        FileObject projectDir = project.getProjectDirectory();

        return projectDir.getPath();
    }

    private boolean dataSetAlreadyOpened(String dataSetName) {

        Lookup lk = Lookup.getDefault().lookup(DataSetTracker.class
        ).getLookup();

        Collection<? extends DataSet> data = lk.lookupAll(DataSet.class);
        for (DataSet ds : data) {
            if (ds.toString().equals(dataSetName)) {
                return true;
            }
        }

        return false;
    }

    private TreeFilter loadTreeFilter(File dir, String file) {
        TreeFilter tf = new TreeFilter();
        TreeSet<String>[] filterSet = null;
        try (FileReader reader = new FileReader(new File(dir, file))) {

            JSONParser parser = new JSONParser();
            JSONObject json;
            json = (JSONObject) parser.parse(reader);

            tf.datamodel = (String) json.get("datamodel");
            JSONObject bounds = (JSONObject) json.get("bounds");
            JSONArray filter = (JSONArray) json.get("filter");
            int colSize = (int) (long) bounds.get("col-size");
            filterSet = new TreeSet[colSize];
            for (int i = 0; i < filterSet.length; i++) {
                filterSet[i] = new TreeSet<String>();
            }
            for (Iterator<JSONObject> it = filter.iterator(); it.hasNext();) {
                JSONObject obj = it.next();
                int c = (int) (long) obj.get("col");
                String value = (String) obj.get("value");
                filterSet[c].add(value);
            }
        } catch (IOException | ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
        tf.filter = filterSet;
        return tf;
    }

    private CategoryProvider loadCategoryPalette(File visualization, String palette, DataModel dm, TreeSet<String>[] filter) {

        CategoryProvider catProvider = new CategoryProvider(dm);
        catProvider.setSelection(filter);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(visualization, palette)));
            String line;
            int i = 0;
            Iterator<Map.Entry<String, Integer>> si = catProvider.getCategories().iterator();
            while ((line = br.readLine()) != null && i < catProvider.getCategories().size()) {
                String[] newColors = line.split(",");
                if (newColors.length == 3) {
                    Color c = new Color(Integer.parseInt(newColors[0]), Integer.parseInt(newColors[1]), Integer.parseInt(newColors[2]));
                    catProvider.putCategoryColor(si.next().getKey(), c);
                    i++;
                }
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return catProvider;

    }

    private JDialog createLoadingDialog() {
        JDialog d = new JDialog(WindowManager.getDefault().getMainWindow(), null, true);
        d.setLayout(new GridBagLayout());
        JPanel panel = new JPanel();
        JLabel jlabel = new JLabel("Loading DataSet.....");
        jlabel.setFont(new Font("Verdana", 1, 14));
        panel.add(jlabel);
        d.add(panel, new GridBagConstraints());
        d.setSize(200, 50);
        d.setLocationRelativeTo(null);
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        return d;
    }

}

class TreeFilter {

    TreeSet<String>[] filter;
    String datamodel;
}
