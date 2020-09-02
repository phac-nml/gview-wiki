import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import org.biojava.bio.seq.FeatureFilter;
import org.biojava.bio.seq.StrandedFeature;

import ca.corefacility.gview.data.GenomeData;
import ca.corefacility.gview.data.Slot;
import ca.corefacility.gview.data.readers.GViewDataParseException;
import ca.corefacility.gview.data.readers.GViewFileData;
import ca.corefacility.gview.data.readers.GViewFileReader;
import ca.corefacility.gview.layout.sequence.LayoutFactory;
import ca.corefacility.gview.layout.sequence.linear.LayoutFactoryLinear;
import ca.corefacility.gview.main.GUIManager;
import ca.corefacility.gview.map.GViewMap;
import ca.corefacility.gview.map.GViewMapFactory;
import ca.corefacility.gview.style.GlobalStyle;
import ca.corefacility.gview.style.MapStyle;
import ca.corefacility.gview.style.datastyle.DataStyle;
import ca.corefacility.gview.style.datastyle.FeatureHolderStyle;
import ca.corefacility.gview.style.datastyle.SlotStyle;
import ca.corefacility.gview.style.items.BackboneStyle;
import ca.corefacility.gview.style.items.RulerStyle;
import ca.corefacility.gview.style.items.TooltipStyle;
import ca.corefacility.gview.textextractor.GeneTextExtractor;
import ca.corefacility.gview.textextractor.LocationExtractor;

public class apiexample1
{
	private static final long serialVersionUID = -920106572049225781L;
	
	private static MapStyle buildStyle()
	{
		/**Global Style**/
		
		MapStyle mapStyle = new MapStyle();
		
		// extract associated GlobalStyle from newly created MapStyle object
		GlobalStyle global = mapStyle.getGlobalStyle();
		
		// set initial height/width of the map
		global.setDefaultHeight(700);
		global.setDefaultWidth(700);
		
		global.setBackgroundPaint(Color.WHITE);
		
		// extract tooltip style from global style
		TooltipStyle tooltip = global.getTooltipStyle();
		tooltip.setFont(new Font("SansSerif", Font.PLAIN, 12));
		tooltip.setBackgroundPaint(new Color(0.4f,0.4f,1.0f,1.0f));
		tooltip.setTextPaint(Color.BLACK);
		
		// extract style information dealing with the backbone
		BackboneStyle backbone = global.getBackboneStyle();
		backbone.setPaint(Color.GRAY.darker());
		backbone.setThickness(5.0);
		
		// extract information dealing with the ruler style
		RulerStyle ruler = global.getRulerStyle();
		ruler.setTickDensity(0.5f);
		ruler.setMajorTickLength(5.0);
		ruler.setMinorTickLength(1.5);
		ruler.setTickThickness(2.0);
		ruler.setMinorTickPaint(Color.LIGHT_GRAY);
		ruler.setMajorTickPaint(Color.DARK_GRAY);
		ruler.setFont(new Font("SansSerif", Font.PLAIN, 12));
		ruler.setTextPaint(Color.BLACK);
		
		/**Slots**/
		
		// assumes mapStyle created as above
		
		// extract data style from map style
		DataStyle dataStyle = mapStyle.getDataStyle();
		
		// creates the first two slots
		SlotStyle positiveSlot = dataStyle.createSlotStyle(Slot.FIRST_UPPER);
		positiveSlot.setToolTipExtractor(new GeneTextExtractor());
		SlotStyle negativeSlot = dataStyle.createSlotStyle(Slot.FIRST_LOWER);
		negativeSlot.setToolTipExtractor(new GeneTextExtractor());
		
		/**FeatureHolderStyle**/
		
		// creates a feature holder style in the first upper slot containing all the features
		FeatureHolderStyle positive = positiveSlot.createFeatureHolderStyle(new FeatureFilter.StrandFilter(StrandedFeature.POSITIVE));
		positive.setThickness(0.7);
		positive.setTransparency(0.5f);
		positive.setToolTipExtractor(new LocationExtractor());
		positive.setPaint(Color.BLUE);
		
		// creates a holder containing all negative features in the first lower slot
		FeatureHolderStyle negativeFeatures = negativeSlot.createFeatureHolderStyle(new FeatureFilter.StrandFilter(StrandedFeature.NEGATIVE));
		negativeFeatures.setPaint(Color.RED);
		
		return mapStyle;
	}
	
	
	public static void main(String[] args)
	{
		try
		{
			// read in genome data
			GViewFileData fileData = GViewFileReader.read("example_data/NC_007622.gbk");
			
			GenomeData data = fileData.getGenomeData();
			
	  		MapStyle style = buildStyle(); // creates style, buildStyle() code not shown
			LayoutFactory layoutFactory = new LayoutFactoryLinear();
	  		
	  		GViewMap gViewMap = GViewMapFactory.createMap(data, style, layoutFactory);
	  		
	  		// builds a frame which can be used to display/navigate around on the map
			GUIManager.getInstance().buildGUIFrame("GView", gViewMap);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (GViewDataParseException e)
		{
			e.printStackTrace();
		}
	}
}
