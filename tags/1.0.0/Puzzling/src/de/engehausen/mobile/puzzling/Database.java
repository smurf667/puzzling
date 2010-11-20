package de.engehausen.mobile.puzzling;

import java.io.UnsupportedEncodingException;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * Primitive management of the image sources (in-jar references
 * start with a slash, others with a protocol, typically <code>http:</code>
 * or <code>file:</code>). In addition to the potentially sixteen
 * image sources three "high scores" for each difficulty level
 * are stored.
 * <p>The "store" is return as <code>String</code> or <code>int</code>
 * arrays, which means they can be directly written to. This is
 * done for convenience only in this small piece of code - you wouldn't
 * want this "normally".</p>
 */
public final class Database {
	
	private static final String STORE_NAME = "puzzling";

	private final RecordStore store;
	
	private static final String EMPTY = "";
	private static final String CHARSET = "UTF-8";
	private static final int MAX_SECONDS_INT = 5999;
	private static final String MAX_SECONDS = Integer.toString(MAX_SECONDS_INT);

	private static final String[] DEFAULTS = {
		"/4427417620.png",
		"/2089504883.png",
		"/2567006179.png",
		"/321902538.png",
		"http://www.engehausen.de/animals.png",
		"http://www.engehausen.de/teddy.png",
		EMPTY,
		EMPTY,
		EMPTY,
		EMPTY,
		EMPTY,
		EMPTY,
		EMPTY,
		EMPTY,
		EMPTY,
		EMPTY
	};
	
	private final String[] sources;
	private final int[] scores;
	
	/**
	 * Creates an instance of the database. If there is a problem
	 * with the record store, a version that returns default values
	 * only and that does not save anything is returned.
	 * @return an instance of the database.
	 */
	public static Database createDatabase() {
		Database result;
		try {
			result = new Database(true);
		} catch (RecordStoreException e) {
			result = new Database();
		}
		return result;
	}
	
	/**
	 * Creates the database backed by the "puzzling" record
	 * store.
	 * @param create whether or not to create the store, should it not exist
	 * @throws RecordStoreException in case of error
	 */
	private Database(final boolean create) throws RecordStoreException {
		store = RecordStore.openRecordStore(STORE_NAME, create);
		if (store.getNumRecords() == 0) {
			initDefaults(store);
		}
		sources = new String[DEFAULTS.length];
		scores = new int[3];
	}

	/**
	 * Creates a database with default values in which
	 * saving has no effect.
	 */
	private Database() {
		store = null;
		sources = new String[DEFAULTS.length];
		System.arraycopy(DEFAULTS, 0, sources, 0, sources.length);
		scores = new int[] { MAX_SECONDS_INT, MAX_SECONDS_INT, MAX_SECONDS_INT };
	}

	/**
	 * Returns a reference to the internally held image sources.
	 * {@link #load()} must have been called before.
	 * @return the image sources, never <code>null</code>.
	 */
	public String[] getSources() {
		return sources;
	}

	/**
	 * Returns a reference to the internally held scores.
	 * {@link #load()} must have been called before.
	 * @return the scores, never <code>null</code>.
	 */
	public int[] getScores() {
		return scores;
	}
	
	/**
	 * Loads the values from the "database" into internal storage.
	 */
	public void load() {
		if (store != null) {
			for (int i = 0; i < DEFAULTS.length; i++) {
				try {
					final byte[] b = store.getRecord(i + 1);
					if (b != null) {
						try {
							sources[i] = new String(b, CHARSET);
							if (sources[i].length() == 0) {
								sources[i] = null;
							}
						} catch (UnsupportedEncodingException e) {
							// ignore
						}
					} else {
						sources[i] = null;
					}
				} catch (RecordStoreException e) {
					// ignore
				}
			}
			for (int i = 0; i < scores.length; i++) {
				try {
					scores[i] = Integer.parseInt(new String(store.getRecord(i+1+DEFAULTS.length), CHARSET));
				} catch (UnsupportedEncodingException e) {
					scores[i] = 5999;
				} catch (NumberFormatException e) {
					scores[i] = 5999;
				} catch (RecordStoreException e) {
					scores[i] = 5999;
				}
			}			
		}
	}

	/**
	 * Saves the values currently held in the arrays referenced
	 * by {@link #getSources()} and {@link #getScores()}.
	 */
	public void save() {
		if (store != null) {
			try {
				for (int i = 0; i < DEFAULTS.length; i++) {
					final byte[] b;
					if (sources[i] != null) {
						b = sources[i].getBytes(CHARSET);
					} else {
						b = EMPTY.getBytes(CHARSET);
					}
					store.setRecord(i+1, b, 0, b.length);
				}
				for (int i = 0; i < scores.length; i++) {
					final byte[] b = Integer.toString(scores[i]).getBytes(CHARSET);
					store.setRecord(i+1+DEFAULTS.length, b, 0, b.length);
				}			
			} catch (RecordStoreException e) {
				// ignore
			} catch (UnsupportedEncodingException e) {
				// ignore
			}			
		}
	}

	/**
	 * Deletes the persisted data of the "puzzling" record store.
	 */
	public void delete() {
		try {
			if (store != null) {
				store.closeRecordStore();
			}
			RecordStore.deleteRecordStore(STORE_NAME);
		} catch (RecordStoreException e) {
			// ignore
		}
	}

	/**
	 * Initializes the record store with default
	 * values.
	 * @param aStore the store to initialized, must not be <code>null</code>.
	 */
	private void initDefaults(final RecordStore aStore) {
		try {
			for (int i = 0; i < DEFAULTS.length; i++) {
				final byte[] bytes = DEFAULTS[i].getBytes(CHARSET);
				aStore.addRecord(bytes, 0, bytes.length);
			}
			final byte[] bytes = MAX_SECONDS.getBytes(CHARSET);
			aStore.addRecord(bytes, 0, bytes.length);
			aStore.addRecord(bytes, 0, bytes.length);
			aStore.addRecord(bytes, 0, bytes.length);
		} catch (UnsupportedEncodingException e) {
			// ignore
		} catch (RecordStoreException e) {
			// ignore
		}
	}

}
