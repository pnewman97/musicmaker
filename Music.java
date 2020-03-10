import java.util.*;
import jm.JMC;
import jm.music.data.*;
import jm.music.tools.Mod;
import jm.util.*;
import jm.music.data.Note;

public class Music implements JMC {
	public static final int[] CHORD_OPTIONS = {1, 1, 2, 3, 4, 4, 5, 5, 6, 6};
	public static final int[] KEY_OPTIONS = {49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60};
	public static final int[] MAJOR_SCALE = {0, 2, 4, 5, 7, 9, 11};
	public static final int[] EXTENDED_MAJOR_SCALE = {0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 17, 19, 21, 23, 24};
	public static final int[] INSTRUMENT_OPTIONS = {THUMB_PIANO, EPIANO, ELECTRIC_GRAND, CELESTA, CLAV, FANTASIA,
													HARP, ICERAIN, HONKYTONK, KOTO, MARIMBA, PAD, PITZ, POLYSYNTH,
													SHAMISEN, STEEL_DRUM, SWEEP};
	public static List<Integer> chordSequence = null;

	public static void main(String[] args) {
		
		//select random key
		Random rand = new Random();
		int n = rand.nextInt(12);
		int key = KEY_OPTIONS[n];

		Score s = new Score();
		Part p1 = new Part();
		
		//create chord progression
		CPhrase chordProgression = CreateChordProgression(key);	
		
		//create melody, repeat twice
		Phrase melody = CreateMelody(key);
		melody.addNoteList(melody.getNoteArray());
		
		//generate new melody every other time
		for (int i = 0; i < 8; i++) {
			Phrase newMelody= CreateMelody(key);
			melody.addNoteList(newMelody.getNoteArray());
			melody.addNoteList(newMelody.getNoteArray());
		}
		//combine together into single phrase
		chordProgression.addPhrase(melody);
		//add to part and repeat
		p1.addCPhrase(chordProgression);
		Mod.repeat(p1, 16);
		
		
		//select random instrument
		n = rand.nextInt(INSTRUMENT_OPTIONS.length);
		int inst = INSTRUMENT_OPTIONS[n];
		p1.setInstrument(inst);
		
		//change instrument
//		p1.setInstrument(THUMB_PIANO);
//		p1.setInstrument(EPIANO);
//		p1.setInstrument(ELECTRIC_GRAND);
//		p1.setInstrument(CELESTA);
//		p1.setInstrument(CLAV);
//		p1.setInstrument(FANTASIA);
//		p1.setInstrument(HARP);
//		p1.setInstrument(ICERAIN);
//		p1.setInstrument(HONKYTONK);
//		p1.setInstrument(KOTO);
//		p1.setInstrument(MARIMBA);
//		p1.setInstrument(PAD);
//		p1.setInstrument(PITZ);
//		p1.setInstrument(POLYSYNTH);
//		p1.setInstrument(SHAMISEN);
//		p1.setInstrument(STEEL_DRUM);
//		p1.setInstrument(SWEEP);
		
		
		
		//select random tempo
		s.setTempo(rand.nextInt(45) + 50);
		
		//jam
		s.add(p1);
		Play.midi(s);

		
	}
	
	//choose 4 random chords among the I, ii, iii, IV, V, and vi and build a chord
	//progression out of it
	public static CPhrase CreateChordProgression(int keyRoot) {
		chordSequence = new ArrayList<Integer>();
		//chord progression will always include the I chord
		chordSequence.add(1);
		//the other 3 chords will be randomly chosen
		for (int i = 0; i < 3; i++) {
			Random rand = new Random();
			int n = rand.nextInt(CHORD_OPTIONS.length);
			int chord = CHORD_OPTIONS[n];
			chordSequence.add(chord);
		}
		//randomize the order of chords
		Collections.shuffle(chordSequence);
		
		
//use for custom chord progression		
//		chordSequence = new ArrayList();
//		chordSequence.add(2);
//		chordSequence.add(4);
//		chordSequence.add(1);
//		chordSequence.add(5);
		
		//print out chord progression
		for (int i = 0; i < chordSequence.size(); i++) System.out.println(chordSequence.get(i));
		
		
		//create random Rhythm patter for chord progression
		double[] timingOptions = { 1, 1, .5, .5, .25};
		double totalBeats = 0;
		ArrayList<Double> chordRhythm = new ArrayList<Double>();
		while (totalBeats < 2) {
			Random rand = new Random();
			int beat = rand.nextInt(timingOptions.length);
			if (totalBeats + timingOptions[beat] <= 2) {
				chordRhythm.add(timingOptions[beat]);
				totalBeats += timingOptions[beat];
			}
		}
		
		//build chord progression
		CPhrase chords = new CPhrase();
		for (int i = 0; i < chordSequence.size(); i++) {
			//get the number of the chord
			int chordNumber = chordSequence.get(i);
			//figure out how many pitches above root it will be
			int chordRoot = keyRoot + MAJOR_SCALE[chordNumber - 1];
			//build chord with bass note and ensure that root/third/fifth all within same octave by using modulus
			int[] pitchArray = {
								chordRoot - 12,
								keyRoot + MAJOR_SCALE[chordNumber - 1],
								keyRoot + MAJOR_SCALE[(chordNumber - 1 + 2) % 7],
								keyRoot + MAJOR_SCALE[(chordNumber - 1 + 4) % 7]
								};
			

			
			//add chord to progression
			for (int j = 0; j < chordRhythm.size(); j++) {
				chords.addChord(pitchArray, chordRhythm.get(j));
			}
		}
			
			
		return chords;
	}
	
	//given a chord progression, builds a melody on top of it
	public static Phrase CreateMelody(int keyRoot) {
		//first create timing/rhythm pattern for each measure
		//(this will be the same for every measure)
		
//arbitrarily chosen likelihood of choosing either a quarter note(1), eight note(.5), or sixteenth note(.25)		
		double[] timingOptions = { 1, .5, .5, .25, .25, .25};
// uncomment for some mozart shit
//			double[] timingOptions = { .125 };
// uncomment for 3/4 timing (little buggy)
//			double[] timingOptions = { .3333333333 };
		
		//keep grabbing a random choice from timingOptions until we get a total of 2 beats
		//(because the melody for each chord will be 2 beats long)
		double totalBeats = 0;
		ArrayList<Double> melodyRhythm = new ArrayList<Double>();
		while (totalBeats < 2) {
			Random rand = new Random();
			int beat = rand.nextInt(timingOptions.length);
			if (totalBeats + timingOptions[beat] <= 2) {
				melodyRhythm.add(timingOptions[beat]);
				totalBeats += timingOptions[beat];
			}
		}
		
		
		
		//construct melody
		Phrase melody = new Phrase();
		//raise keyRoot an octave so that melody is emphasized
		keyRoot += 12;

		Random rand = new Random();
	
//		for each value that is now in timingOptions, we will now assign a note/pitch to that value		
//		arbitrarily chosen likelihood of choosing any pitch that is in the key
//		notes that are in the chord we are playing have a higher likelihood of being chosen because that sounds better
		Integer prevNote = null;
		for (int i = 0; i < chordSequence.size(); i++) {
			int chordNumber = chordSequence.get(i);
			int[] melodyOptions = {
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1] - 5,
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1] - 5,
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1],
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1],
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1],
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1],
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1],
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 1)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 1) % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 2)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 2)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 2)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 2) % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 2) % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 2) % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 3)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 3) % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 4)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 4)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 4)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 4) % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 4) % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 4) % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 5)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 5) % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1] + 12,
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1] + 12,
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1] + 12,
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 7) % 14],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 7) % 14],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 8) % 14],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 9) % 14],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 9) % 14],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 11) % 14],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 11) % 14],
									};
			
			int note = rand.nextInt(melodyOptions.length);
			for (int j = 0; j < melodyRhythm.size(); j++) {	
				
				//ensure that subsequent melody notes stay close together
				int count = 0;
				while (count < 5 && prevNote != null && Math.abs(melodyOptions[note] - prevNote) > 2) {
					count++;
					note = rand.nextInt(melodyOptions.length);
				}
				while (prevNote != null && Math.abs(melodyOptions[note] - prevNote) > 7) note = rand.nextInt(melodyOptions.length);
				prevNote = melodyOptions[note];	
				
				//create the note and add it to the melody
				Note n = new Note(melodyOptions[note], melodyRhythm.get(j));
				melody.add(n);
				note = rand.nextInt(melodyOptions.length);
			}
		}
		
		return melody;
	}
}
