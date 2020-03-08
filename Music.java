import java.util.*;
import jm.JMC;
import jm.music.data.*;
import jm.music.tools.Mod;
import jm.util.*;
import jm.music.data.Note;

public class Music implements JMC {
	public static final int[] CHORD_OPTIONS = {1, 2, 3, 4, 5, 6};
	public static final int[] KEY_OPTIONS = {49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60};
	public static final int[] MAJOR_SCALE = {0, 2, 4, 5, 7, 9, 11};
	public static final int[] EXTENDED_MAJOR_SCALE = {0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 17, 19, 21, 23, 24};
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
		//create melody
		Phrase melody = CreateMelody(key);
		//combine together into single phrase
		chordProgression.addPhrase(melody);
		//add to part and repeat
		p1.addCPhrase(chordProgression);
		Mod.repeat(p1, 8);

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
//		chordSequence.add(6);
//		chordSequence.add(4);
//		chordSequence.add(1);
//		chordSequence.add(5);
		
		//print out chord progression
		for (int i = 0; i < chordSequence.size(); i++) System.out.println(chordSequence.get(i));
		
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
			//add chord to progression and play twice
			chords.addChord(pitchArray, QN);
			chords.addChord(pitchArray, QN);
		}
			
		return chords;
	}
	
	//given a chord progression, builds a melody on top of it
	public static Phrase CreateMelody(int keyRoot) {
		//first create timing/rhythm pattern for each measure
		//(this will be the same for every measure)
		
//arbitrarily chosen likelihood of choosing either a quarter note(1), eight note(.5), or sixteenth note(.25)		
		double[] timingOptions = { 1, .5, .5, .25, .25, .25, .25, .25, .25};
// uncomment for some mozart shit
//			double[] timingOptions = { .125 };
// uncomment for 3/4
//			double[] timingOptions = { .33333333 };
		
		//keep grabbing a random choice from timingOptions until we get a total of 2
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
		for (int i = 0; i < chordSequence.size(); i++) {
			int chordNumber = chordSequence.get(i);
			int[] melodyOptions = {
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1],
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1],
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1],
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1],
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1],
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1],
//									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 1)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 1) % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 2)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 2)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 2)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 2) % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 2) % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 2) % 7],
//									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 2)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 3)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 3) % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 4)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 4)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 4)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 4) % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 4) % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 4) % 7],
//									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 4)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 5)],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1 + 5) % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1) + 6],
									keyRoot + EXTENDED_MAJOR_SCALE[(chordNumber - 1) + 6 % 7],
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1] + 12,
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1] + 12,
									keyRoot + EXTENDED_MAJOR_SCALE[chordNumber - 1] + 12
									};
			
			int pos = rand.nextInt(melodyOptions.length);
			for (int j = 0; j < melodyRhythm.size(); j++) {	
				int nextPos = rand.nextInt(melodyOptions.length);
				Note n = new Note(melodyOptions[pos], melodyRhythm.get(j));
				melody.add(n);
				//ensure that subsequent melody notes stay close together
				while (Math.abs(melodyOptions[pos] - melodyOptions[nextPos]) > 6) nextPos = rand.nextInt(melodyOptions.length);
				pos = nextPos;
			}
		}
		
		return melody;
	}
}
