import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Vector;

public class GUI {
    static Connector connector = new Connector();
    static JFrame jf = null;
    static JScrollPane sp = null;
    static GridBagLayout grids = null;

    public static void main(String[] args) {
        jf = new JFrame("Dr. Undefined");
        grids = new GridBagLayout();
        GridBagConstraints gridConstraints = new GridBagConstraints();
        jf.setLayout(grids);

        gridConstraints.fill = GridBagConstraints.BOTH;
        gridConstraints.gridx = 1;
        gridConstraints.gridy = 0;
        gridConstraints.weightx = 0.25;
        gridConstraints.weighty = 0.1;
        gridConstraints.gridheight = 1;
        gridConstraints.gridwidth = 1;
        JLabel tl = new JLabel("Please input your command");
        tl.setHorizontalAlignment(0);
        tl.setBorder(BorderFactory.createLineBorder(Color.gray));
        grids.setConstraints(tl, gridConstraints);
        jf.add(tl);

        gridConstraints.fill = GridBagConstraints.BOTH;
        gridConstraints.gridx = 1;
        gridConstraints.gridy = 1;
        gridConstraints.weightx = 0.25;
        gridConstraints.weighty = 0.7;
        gridConstraints.gridheight = 1;
        gridConstraints.gridwidth = 1;
        JTextArea ta = new JTextArea(7, 30);
        ta.setBorder(BorderFactory.createLineBorder(Color.gray));
        grids.setConstraints(ta, gridConstraints);
        jf.add(ta);

        gridConstraints.fill = GridBagConstraints.BOTH;
        gridConstraints.gridx = 0;
        gridConstraints.gridy = 0;
        gridConstraints.weightx = 0.75;
        gridConstraints.weighty = 0.0;
        gridConstraints.gridheight = 3;
        gridConstraints.gridwidth = 1;
        String[][] emptyData = new String[50][5];
        String[] name = {"", "", "", "", ""};
        JTable jt = new JTable(emptyData, name);
        jt.setBorder(BorderFactory.createLineBorder(Color.gray));
        jt.setGridColor(Color.gray);
        sp = new JScrollPane(jt);

        grids.setConstraints(sp, gridConstraints);
        jf.add(sp);

        gridConstraints.fill = GridBagConstraints.BOTH;
        gridConstraints.gridx = 1;
        gridConstraints.gridy = 2;
        gridConstraints.weightx = 0.25;
        gridConstraints.weighty = 0.2;
        gridConstraints.gridheight = 1;
        gridConstraints.gridwidth = 1;
        JButton tb = new JButton("Query");
        tb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String command = ta.getText();
                // System.out.println(command);
                ResultSet results = connector.query(command);
                RefreshTable(results, gridConstraints);
            }
        });
        grids.setConstraints(tb, gridConstraints);
        jf.add(tb);

        jf.setBounds(300, 300, 1200, 600);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void RefreshTable(ResultSet results, GridBagConstraints gridConstraints) {
        try {
            int col_len = results.getMetaData().getColumnCount();
            String[] headers = new String[col_len];
            Vector<String[]> data = new Vector<String[]>();
            for (int i = 1; i <= col_len; i++) {
                headers[i - 1] = results.getMetaData().getColumnName(i);
            }
            while (results.next()) {
                String[] current = new String[col_len];
                for (int i = 1; i <= col_len; i++) {
                    current[i - 1] = String.format("%s", results.getString(i));
                }
                data.add(current);
            }
            jf.remove(sp);
            gridConstraints.fill = GridBagConstraints.BOTH;
            gridConstraints.gridx = 0;
            gridConstraints.gridy = 0;
            gridConstraints.weightx = 0.75;
            gridConstraints.weighty = 0.0;
            gridConstraints.gridheight = 3;
            gridConstraints.gridwidth = 1;

            String[][] dataList = new String[data.size()][col_len];
            for (int i = 0; i < data.size(); i++) {
                dataList[i] = data.get(i);
            }

            JTable jt = new JTable(dataList, headers);

            //jt.setAutoResizeMode(jt.AUTO_RESIZE_OFF);
            jt.setBorder(BorderFactory.createLineBorder(Color.gray));
            jt.setGridColor(Color.gray);
            sp = new JScrollPane(jt);
            sp.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    Dimension containerWidth = null;
                    containerWidth = sp.getSize();
                    int allwidth = jt.getIntercellSpacing().width;
                    for (int i = 0; i < jt.getColumnCount(); i++) {
                        int max = 0;
                        for (int j = 0; j < jt.getRowCount(); j++) {
                            int width = jt.getCellRenderer(j, i)
                                    .getTableCellRendererComponent(jt, jt.getValueAt(j, i), false, false, j, i)
                                    .getPreferredSize().width;
                            if (width > max) {
                                max = width;
                            }
                        }

                        int headerWidth = jt.getTableHeader().getDefaultRenderer()
                                .getTableCellRendererComponent(jt, jt.getColumnModel().getColumn(i).getIdentifier(),
                                        false, false, -1, i).getPreferredSize().width;
                        if(max < headerWidth){
                            max = headerWidth;
                        }
                        jt.getColumnModel().getColumn(i).setPreferredWidth(max);
                        allwidth += max + jt.getIntercellSpacing().width;
                    }
                    allwidth += jt.getIntercellSpacing().width;
                    if (allwidth > containerWidth.width) {
                        jt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    } else {
                        jt.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                    }
                }
            });

            grids.setConstraints(sp, gridConstraints);
            jf.add(sp);
            jf.setVisible(false);
            jf.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(jf, "SQL error! Please check your input command!",
                    "SQL error", 0);
            e.printStackTrace();
        }
    }
}
