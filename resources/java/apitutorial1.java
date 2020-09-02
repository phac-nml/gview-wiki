import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Feature;
import org.biojava.bio.seq.FeatureFilter;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.StrandedFeature;
import org.biojava.bio.seq.impl.SimpleSequenceFactory;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.RangeLocation;
import org.biojava.bio.symbol.SymbolList;

import ca.corefacility.gview.data.BlankSymbolList;
import ca.corefacility.gview.data.GenomeData;
import ca.corefacility.gview.data.GenomeDataFactory;
import ca.corefacility.gview.data.Slot;
import ca.corefacility.gview.layout.sequence.LayoutFactory;
import ca.corefacility.gview.layout.sequence.circular.LayoutFactoryCircular;
import ca.corefacility.gview.layout.sequence.linear.LayoutFactoryLinear;
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
import ca.corefacility.gview.textextractor.LocationExtractor;
import ca.corefacility.gview.writers.ImageWriter;
import ca.corefacility.gview.writers.ImageWriterFactory;
import edu.umd.cs.piccolo.PCanvas;

public class apitutorial1
{
	/**
	 * @return  The style used to build the GView map.
	 */
	private static MapStyle buildStyle()
	{
		/**Global Style**/
		
		MapStyle mapStyle = new MapStyle();
		
		// Extract associated GlobalStyle from newly created MapStyle object
		GlobalStyle global = mapStyle.getGlobalStyle();
		
		// Set initial height/width of the map
		global.setDefaultWidth(1200);
		global.setDefaultHeight(900);
		
		global.setBackgroundPaint(Color.WHITE);
		
		// Set tool tip style.  This is used to display extra information about various items on the map.
		TooltipStyle tooltip = global.getTooltipStyle();
		tooltip.setFont(new Font("SansSerif", Font.PLAIN, 12));
		tooltip.setBackgroundPaint(new Color(134, 134, 255)); // set background paint to (r,g,b)=(134,134,255)
		tooltip.setOutlinePaint(new Color(0.0f, 0.0f, 0.0f, 0.5f)); // set outline paint of tool tip item to (r,g,b) = (0,0,0) and alpha=0.5
		tooltip.setTextPaint(Color.BLACK);  // set the paint of the text for the tool tip item
		
		// Set style information dealing with the backbone.  This is the item displayed in the very centre of the slots.
		BackboneStyle backbone = global.getBackboneStyle();
		backbone.setPaint(Color.GRAY.darker());
		backbone.setThickness(2.0);
		backbone.setInitialBackboneLength(1100); // sets the "length" of the backbone along which the various items are to be displayed
		
		// Set information dealing with the ruler style
		RulerStyle ruler = global.getRulerStyle();
		ruler.setMajorTickLength(15.0);  //
		ruler.setMinorTickLength(5.0);
		ruler.setTickDensity(0.5f);
		ruler.setTickThickness(2.0);
		ruler.setMinorTickPaint(Color.GREEN.darker().darker());
		ruler.setMajorTickPaint(Color.GREEN.darker().darker());
		ruler.setFont(new Font("SansSerif", Font.BOLD, 12));  // font/font paint set information dealing with the text label of the ruler
		ruler.setTextPaint(Color.BLACK);
		
		/**Slots**/
		
		// Assumes mapStyle created as above
		
		// Extract data style from map style
		DataStyle dataStyle = mapStyle.getDataStyle();
		
		// Creates the first two slots
		SlotStyle firstUpperSlot = dataStyle.createSlotStyle(Slot.FIRST_UPPER); // first slot above backbone
		SlotStyle firstLowerSlot = dataStyle.createSlotStyle(Slot.FIRST_LOWER); // first slot below backbone
		
		// Creates slots right next to the first slots
		SlotStyle secondUpperSlot = dataStyle.createSlotStyle(Slot.FIRST_UPPER + 1);
		SlotStyle secondLowerSlot = dataStyle.createSlotStyle(Slot.FIRST_LOWER - 1);
		
		// Sets the default color of any features in these slots
		firstUpperSlot.setPaint(Color.BLACK);
		firstLowerSlot.setPaint(Color.BLACK);
		secondUpperSlot.setPaint(Color.BLACK);
		secondLowerSlot.setPaint(Color.BLACK);
		
		 // Sets the thickness of the respective slots
		firstUpperSlot.setThickness(15);
		firstLowerSlot.setThickness(15);
		secondUpperSlot.setThickness(15);
		secondLowerSlot.setThickness(15);
		
		/**FeatureHolderStyle**/
		
		// Assumes SlotStyles were created as above
		
		// Creates a feature holder style in the first upper slot containing all the positive stranded features
		FeatureHolderStyle positiveFeatures = firstUpperSlot.createFeatureHolderStyle(new FeatureFilter.StrandFilter(StrandedFeature.POSITIVE));
		positiveFeatures.setThickness(0.7); // sets the thickness of these features as a proportion of the thickness of the slot
		positiveFeatures.setTransparency(0.9f); // sets transparency of all features drawn within this slot
		positiveFeatures.setToolTipExtractor(new LocationExtractor()); 	// sets how to extract text to be displayed for tool tips on these features
		positiveFeatures.setPaint(Color.BLUE); // sets default color of the positive features
		
		// Creates a sub feature holder style below the positiveFeatures feature holder.  This can be used to extract and override style information
		//	for features that would be contained within the positiveFeatures feature holder.
		// In this example, we will override the blue color for positive features that overlap bases [50,95] and color them a darker blue.
		FeatureHolderStyle subPositiveFeatures = positiveFeatures.createFeatureHolderStyle(new FeatureFilter.OverlapsLocation(new RangeLocation(50,95)));
		subPositiveFeatures.setPaint(Color.BLUE.darker().darker().darker().darker()); // overrides the color for these features, making it darker
		
		// Creates a holder containing all negative features in the first lower slot
		FeatureHolderStyle negativeFeatures = firstLowerSlot.createFeatureHolderStyle(new FeatureFilter.StrandFilter(StrandedFeature.NEGATIVE));
		negativeFeatures.setThickness(0.7);
		negativeFeatures.setToolTipExtractor(new LocationExtractor());
		negativeFeatures.setPaint(Color.RED);
		
		// Creates a holder containing all features in the second upper slot
		FeatureHolderStyle allFeatures = secondUpperSlot.createFeatureHolderStyle(FeatureFilter.all);
		allFeatures.setThickness(0.7);
		allFeatures.setToolTipExtractor(new LocationExtractor());
		allFeatures.setPaint(Color.GREEN);
		
		// Creates a holder containing only features overlapping bases [0,50] in the second lower slot
		FeatureHolderStyle locationFeatures = secondLowerSlot.createFeatureHolderStyle(new FeatureFilter.OverlapsLocation(new RangeLocation(0,50)));
		locationFeatures.setThickness(0.7);
		locationFeatures.setToolTipExtractor(new LocationExtractor());
		locationFeatures.setPaint(Color.BLACK);
		
		return mapStyle;
	}
	
	/**
	 * @return  The data that will be displayed on the map.
	 */
	private static GenomeData buildData()
	{
		GenomeData data = null;
		
		// Create a blank symbol list with a length of 110
		SymbolList blankList = new BlankSymbolList(110);
		
		try
		{
			// Create a factory to build the blank sequence
			SimpleSequenceFactory seqFactory = new SimpleSequenceFactory();
			
			// Creates a sequence from the blank symbol list
			Sequence blankSequence = seqFactory.createSequence(blankList, null, null, null);
			
			// Creates a basic feature located at the beginning of the sequence
			Feature.Template basic = new Feature.Template();
			basic.location = new RangeLocation(1,9);
			blankSequence.createFeature(basic);
			
			// Creates a basic feature located at the end of the sequence
			basic.location = new RangeLocation(91,94);
			blankSequence.createFeature(basic);
			
			// Creates a positive stranded feature
			StrandedFeature.Template stranded = new StrandedFeature.Template();
			stranded.strand = StrandedFeature.POSITIVE;
			stranded.location = new RangeLocation(11,19);
			blankSequence.createFeature(stranded);
			
			// Creates another positive stranded feature
			stranded.location = new RangeLocation(31,39);
			blankSequence.createFeature(stranded);
			
			// and another positive stranded feature
			stranded.location = new RangeLocation(51,59);
			blankSequence.createFeature(stranded);
			
			// and another positive stranded feature
			stranded.location = new RangeLocation(71,79);
			blankSequence.createFeature(stranded);
			
			// Creates a negative stranded feature
			stranded.strand = StrandedFeature.NEGATIVE;
			stranded.location = new RangeLocation(21,29);
			blankSequence.createFeature(stranded);
			
			// negative stranded feature
			stranded.location = new RangeLocation(41,49);
			blankSequence.createFeature(stranded);
			
			// negative stranded feature
			stranded.location = new RangeLocation(61,69);
			blankSequence.createFeature(stranded);
			
			// negative stranded feature
			stranded.location = new RangeLocation(81,89);
			blankSequence.createFeature(stranded);
			
			// Creates a GenomeData object from the Sequence
			data = GenomeDataFactory.createGenomeData(blankSequence);
		}
		catch (IllegalSymbolException ex)
		{
			ex.printStackTrace();
		}
		catch (BioException be)
		{
			be.printStackTrace();
		}
		
		return data;
	}
	
	public static void main(String[] args)
	{
		// Creates the 3 components necessary to build the map
		GenomeData data = buildData();
		MapStyle style = buildStyle();
		LayoutFactory layoutFactory;
		
		// Change this to change between linear/circular layouts
		layoutFactory = new LayoutFactoryLinear();
//		layoutFactory = new LayoutFactoryCircular();
		
		// Builds the map from the data/style/layout information
		GViewMap gViewMap = GViewMapFactory.createMap(data, style, layoutFactory);
		
		// Sets the size that we view the map as
		gViewMap.setViewSize(1200, 600);
		
		// Centres the map within the view
		gViewMap.centerMap();
				
		// Builds an ImageWriter used to write the map to a file using different formats
		ImageWriter writerPNG = ImageWriterFactory.createImageWriter("png");
		ImageWriter writerSVG = ImageWriterFactory.createImageWriter("svg");
		
		// Writes the map to a file
		try
		{
			writerPNG.writeToImage(gViewMap, "example1.png");
			writerSVG.writeToImage(gViewMap, "example1.svg");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
