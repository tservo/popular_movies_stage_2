package com.example.android.popularmovies.data;

public class Trailer {

    private final String id; // trailer id
    private final int mId; // the movie id
    private final String key; // video key
    private final String name; // trailer name
    private final String site; // site
    private final int size; // video size
    private final String type; // trailer type

    /**
     * Constructor
     * @param id trailer id
     * @param mId movie id
     * @param key video key
     * @param name trailer name
     * @param site video site
     * @param size video size
     * @param type trailer type
     */
    public Trailer(String id,
                   int mId,
                   String key,
                   String name,
                   String site,
                   int size,
                   String type
    ) {
        this.id = id;
        this.mId = mId;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    /**
     * getter for name var
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * getter for size type
     * @return video size
     */
    public int getSize() {
        return size;
    }
}
