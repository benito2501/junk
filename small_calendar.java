import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class small_calendar implements Runnable{

	private JFrame main_frame;
	private JPanel box_panel;
	private JPanel upper;
	private JPanel center;
	private JPanel lower;
	private final String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	private Calendar calendar;
	private JComboBox<String> month_list;
	private JSpinner year_spin;
	private JComboBox<Integer> day_list;
	private final JButton get_current = new JButton("Get Current Date");
	private final JLabel day_label = new JLabel("Day");
	private final JLabel month_label = new JLabel("Month");
	private final JLabel year_label = new JLabel("Year");
	private final JButton day_minus = new JButton("-");
	private final JButton day_plus = new JButton("+");
	private final JButton month_minus = new JButton("-");
	private final JButton month_plus = new JButton("+");
	private final JButton year_minus = new JButton("-");
	private final JButton year_plus = new JButton("+");
	private final JTextField day_field = new JTextField("0");
	private final JTextField month_field = new JTextField("0");
	private final JTextField year_field = new JTextField("0");
	private int days_out = 0;
	private int months_out = 0;
	private int years_out = 0;

	public void run(){
		main_frame = new JFrame("Small Calendar");
		main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main_frame.addMouseListener(new unlock_fields() );
		box_panel = new JPanel();
		box_panel.setLayout(new BoxLayout(box_panel, BoxLayout.Y_AXIS) );

		String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
		if(ids.length == 0)
			System.exit(0);
		SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);
		pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
		pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
		calendar = new GregorianCalendar(pdt);

		calendar.setTime(new Date() );

		month_list = new JComboBox<>(months);
		month_list.setSelectedIndex(calendar.get(Calendar.MONTH) );
		month_list.setActionCommand("month_list");
		month_list.addActionListener(new change_date() );
		SpinnerModel yearModel = new SpinnerNumberModel(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) - 1000, calendar.get(Calendar.YEAR) + 1000, 1);
		year_spin = new JSpinner(yearModel);
		year_spin.setEditor(new JSpinner.NumberEditor(year_spin, "#") );
		year_spin.addChangeListener(new year_change() );
		day_list = new JComboBox<>();
		day_list.setModel(new DefaultComboBoxModel<Integer>(get_days(calendar.getActualMaximum(Calendar.DAY_OF_MONTH) ) ) );
		day_list.setSelectedIndex(calendar.get(Calendar.DAY_OF_MONTH)-1 );
		day_list.setActionCommand("day_list");
		day_list.addActionListener(new change_date() );
		upper = new JPanel(new FlowLayout(FlowLayout.CENTER) );
		upper.add(month_list);
		upper.add(day_list);
		upper.add(year_spin);

		center = new JPanel(new FlowLayout(FlowLayout.CENTER) );
		get_current.addActionListener(new get_current_date() );
		center.add(get_current);

		day_minus.setActionCommand("minus");
		day_minus.addActionListener(new day_buttons() );
		day_plus.setActionCommand("plus");
		day_plus.addActionListener(new day_buttons() );
		day_field.addKeyListener(new no_nums() );
		month_minus.setActionCommand("minus");
		month_minus.addActionListener(new month_buttons() );
		month_plus.setActionCommand("plus");
		month_plus.addActionListener(new month_buttons() );
		month_field.addKeyListener(new no_nums() );
		year_minus.setActionCommand("minus");
		year_minus.addActionListener(new year_buttons() );
		year_plus.setActionCommand("plus");
		year_plus.addActionListener(new year_buttons() );
		year_field.addKeyListener(new no_nums() );
		lower = new JPanel(new GridLayout(3, 4) );
		lower.add(day_label);
		lower.add(day_minus);
		lower.add(day_plus);
		lower.add(day_field);
		lower.add(month_label);
		lower.add(month_minus);
		lower.add(month_plus);
		lower.add(month_field);
		lower.add(year_label);
		lower.add(year_minus);
		lower.add(year_plus);
		lower.add(year_field);

		box_panel.add(upper);
		box_panel.add(center);
		box_panel.add(lower);
		main_frame.add(box_panel);
		main_frame.pack();
		main_frame.setVisible(true);
	}

	private Integer[] get_days(int days){
		Integer[] product = new Integer[days];
		for(int d=0;d<days;d++)
			product[d] = d+1;
		return product;
	}

	private void set_calendar(){
		System.out.println(calendar.getTime().toString() );
		int m = calendar.get(Calendar.MONTH);
		//month_list.setSelectedIndex( );
		int d = calendar.get(Calendar.DAY_OF_MONTH);
		day_list.setSelectedIndex(d-1);
		//year_spin.setValue(Integer.valueOf(calendar.get(Calendar.YEAR) ) );
		System.out.println(calendar.getTime().toString() );
	}

	private void reset_days(){
		day_list.setModel(new DefaultComboBoxModel<Integer>(get_days(calendar.getActualMaximum(Calendar.DAY_OF_MONTH) ) ) );
		calendar.set(Calendar.DAY_OF_MONTH, 1 );
	}

	private class no_nums extends KeyAdapter{
		public void keyPressed(KeyEvent ke) {
            if (ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9') {
            	day_field.setEditable(true);
            	month_field.setEditable(true);
            	year_field.setEditable(true);
            }
            else if(ke.getKeyCode()==KeyEvent.VK_BACK_SPACE ){
            	day_field.setEditable(true);
            	month_field.setEditable(true);
            	year_field.setEditable(true);
            }
            else {
            	day_field.setEditable(false);
            	month_field.setEditable(false);
            	year_field.setEditable(false);
            }
         }
	}

	private class unlock_fields extends MouseAdapter{
		public void mouseClicked(MouseEvent e){
			if(!day_field.isFocusOwner() ){
				day_field.setEditable(true);
				month_field.setEditable(true);
            	year_field.setEditable(true);
			}
		}
	}

	private class day_buttons implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			System.out.println(calendar.getTime().toString() );
			int amount;
			if(day_field.getText().equals("") )
				amount = 0;
			else
				amount = Integer.parseInt(day_field.getText() );
			if(ae.getActionCommand().equals("minus") )
				calendar.add(Calendar.DAY_OF_MONTH, (amount * -1) );
			else
				calendar.add(Calendar.DAY_OF_MONTH, amount );
			set_calendar();
			System.out.println(calendar.getTime().toString() );
		}
	}

	private class month_buttons implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			int amount;
			if(month_field.getText().equals("") )
				amount = 0;
			else
				amount = Integer.parseInt(month_field.getText() );
			if(ae.getActionCommand().equals("minus") )
				calendar.add(Calendar.MONTH, (amount * -1) );
			else
				calendar.add(Calendar.MONTH, amount );
			reset_days();
			set_calendar();
		}
	}

	private class year_buttons implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			int amount;
			if(year_field.getText().equals("") )
				amount = 0;
			else
				amount = Integer.parseInt(year_field.getText() );
			if(ae.getActionCommand().equals("minus") )
				calendar.add(Calendar.YEAR, (amount * -1) );
			else
				calendar.add(Calendar.YEAR, amount );
			reset_days();
			set_calendar();
		}
	}

	private class get_current_date implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			calendar.setTime(new Date() );	
			//reset_days();
			set_calendar();
		}
	}

	private class change_date implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			if(ae.getActionCommand().equals("month_list") ){
				JComboBox b = (JComboBox)ae.getSource();
				calendar.set(Calendar.MONTH, b.getSelectedIndex() );
				reset_days();
			}
			else if(ae.getActionCommand().equals("day_list") ){
				JComboBox b = (JComboBox)ae.getSource();
				calendar.set(Calendar.DAY_OF_MONTH, b.getSelectedIndex() );
			}
			
		}
	}

	private class year_change implements ChangeListener{
		public void stateChanged(ChangeEvent e){
			Integer temp = (Integer)year_spin.getValue();
			calendar.set(Calendar.YEAR, temp.intValue() );
			reset_days();
		}
	}

	public static void main(String args[] ){
		EventQueue.invokeLater(new small_calendar() );
	}
}