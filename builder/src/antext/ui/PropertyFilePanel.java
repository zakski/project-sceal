package antext.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import antext.PropertyDef;
import antext.PropertyFile;
import antext.ui.propertyfield.BooleanPropertyField;
import antext.ui.propertyfield.FilePropertyField;
import antext.ui.propertyfield.PropertyField;
import antext.ui.propertyfield.StringPropertyField;

@SuppressWarnings("serial")
public class PropertyFilePanel extends JPanel {
	
	private static final Dimension DIM_PROPERTY_DISPLAY = new Dimension(150, 25);
	private static final Border BRD_PROPERTY_EDIT_FIELD = new EmptyBorder(5, 10, 5, 10);
	
	private PropertyFile propertyFile; 
	
	private ArrayList<PropertyField> editFields;
	
	public PropertyFilePanel(PropertyFile propertyFile) {
		this.propertyFile = propertyFile;
		this.editFields = new ArrayList<>();
		initComponents();
	}
	
	public void updatePropertyFile() throws IOException {
		for(PropertyField field : editFields) {
			propertyFile.getProperties().setProperty(field.getPropertyName(), field.getValue());
		}
		propertyFile.store();
	}
	
	private void initComponents() {
		setLayout(new BorderLayout());
		
		JPanel north = new JPanel(new GridLayout(propertyFile.getProperties().size(), 1));
		
		for(Map.Entry<Object, Object> p : propertyFile.getProperties().entrySet()) {
			JPanel container = new JPanel(new BorderLayout());
			String propertyName = p.getKey().toString();
			String propertyDisplay = propertyFile.getPropertyDisplay(propertyName);
			
			PropertyField editField = null;
			
			switch(propertyFile.getPropertyType(propertyName)) {
			case PropertyDef.TYPE_STRING: 
				editField = new StringPropertyField(propertyFile, propertyName); 
				break;
			case PropertyDef.TYPE_FILE:
				editField = new FilePropertyField(propertyFile, propertyName);
				break;
			case PropertyDef.TYPE_DIRECTORY:
				editField = new FilePropertyField(propertyFile, propertyName, true);
				break;
			case PropertyDef.TYPE_BOOLEAN:
				editField = new BooleanPropertyField(propertyFile, propertyName);
				break;
			default:
				editField = new StringPropertyField(propertyFile, propertyName);
			}
			
			editFields.add(editField);
			
			JLabel lblDisplay = new JLabel(propertyDisplay);
			lblDisplay.setMinimumSize(DIM_PROPERTY_DISPLAY);
			lblDisplay.setMaximumSize(DIM_PROPERTY_DISPLAY);
			lblDisplay.setPreferredSize(DIM_PROPERTY_DISPLAY);
			
			container.add(lblDisplay, BorderLayout.WEST);
			container.add(editField, BorderLayout.CENTER);
			
			container.setBorder(BRD_PROPERTY_EDIT_FIELD);
			
			north.add(container);
		}
		
		add(north, BorderLayout.NORTH);
	}
}
