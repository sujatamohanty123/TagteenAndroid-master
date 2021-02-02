package in.tagteen.tagteen.chatting.emoji;

import android.util.SparseIntArray;

import in.tagteen.tagteen.chatting.R;

/**
 * Created by tony00 on 3/3/2019.
 */
public class Emoji {

    public static final int LAUGHING = 801;
    public static final int LAUGHING_LOUD = 802;
    public static final int CHEEKY = 803;
    public static final int CRYING = 804;
    public static final int FUMING_ANGER = 805;
    public static final int ASTONISH = 806;
    public static final int RELIEVED = 807;
    public static final int SLEEPY = 808;
    public static final int SMIRKING = 809;
    public static final int THINKING = 810;
    public static final int PILE_OF_POO = 811;
    public static final int KISS = 812;
    public static final int KISSES = 813;
    public static final int KISSING = 814;
    public static final int BLOW_KISS = 815;
    public static final int IN_LOVE = 816;
    public static final int HAPPINESS_WITH_LOVE = 817;
    public static final int COOL = 818;
    public static final int HUNGRY = 819;
    public static final int FLIRTY = 820;
    public static final int GOBLIN = 821;
    public static final int SCREAMING_IN_SHOCK = 822;
    public static final int ALIEN = 823;
    public static final int ANGRY = 824;
    public static final int SNEEZE = 825;
    public static final int CLOWN = 826;
    public static final int GHOST = 827;
    public static final int MONSTER = 828;
    public static final int DEVIL = 829;
    public static final int THUMBS_DOWN = 830;
    public static final int THUMBS_UP = 831;
    public static final int PUNCH = 832;
    public static final int APPLAUSE = 833;
    public static final int COUGHING = 834;

    private static final SparseIntArray emoijs = new SparseIntArray();

    static {
      emoijs.put(LAUGHING, R.drawable.emoji_laughing);
      emoijs.put(LAUGHING_LOUD, R.drawable.emoji_laughing_out_loud);
      emoijs.put(CHEEKY, R.drawable.emoji_cheeky);
      emoijs.put(CRYING, R.drawable.emoji_crying);
      emoijs.put(FUMING_ANGER, R.drawable.emoji_fuming_anger);
      emoijs.put(ASTONISH, R.drawable.emoji_astonish);
      emoijs.put(RELIEVED, R.drawable.emoji_relieved);
      emoijs.put(SLEEPY, R.drawable.emoji_sleepy);
      emoijs.put(SMIRKING, R.drawable.emoji_smirking);
      emoijs.put(THINKING, R.drawable.emoji_thinking);
      emoijs.put(PILE_OF_POO, R.drawable.emoji_pile_of_poo);
      emoijs.put(KISS, R.drawable.emoji_kiss);
      emoijs.put(KISSES, R.drawable.emoji_kisses);
      emoijs.put(KISSING, R.drawable.emoji_kissing);
      emoijs.put(BLOW_KISS, R.drawable.emoji_blow_kiss);
      emoijs.put(IN_LOVE, R.drawable.emoji_in_love);
      emoijs.put(HAPPINESS_WITH_LOVE, R.drawable.emoji_happiness_with_love);
      emoijs.put(COOL, R.drawable.emoji_cool);
      emoijs.put(HUNGRY, R.drawable.emoji_hungry);
      emoijs.put(FLIRTY, R.drawable.emoji_flirty);
      emoijs.put(GOBLIN, R.drawable.emoji_goblin);
      emoijs.put(SCREAMING_IN_SHOCK, R.drawable.emoji_screaming_in_shock);
      emoijs.put(ALIEN, R.drawable.emoji_alien);
      emoijs.put(ANGRY, R.drawable.emoji_angry);
      emoijs.put(SNEEZE, R.drawable.emoji_sneeze);
      emoijs.put(CLOWN, R.drawable.emoji_clown);
      emoijs.put(GHOST, R.drawable.emoji_ghost);
      emoijs.put(MONSTER, R.drawable.emoji_monster);
      emoijs.put(DEVIL, R.drawable.emoji_devil);
      emoijs.put(THUMBS_DOWN, R.drawable.emoji_thumbs_down);
      emoijs.put(THUMBS_UP, R.drawable.emoji_thumbs_up);
      emoijs.put(PUNCH, R.drawable.emoji_punch);
      emoijs.put(APPLAUSE, R.drawable.emoji_applause);
      emoijs.put(COUGHING, R.drawable.emoji_coughing);
    }

    private static final SparseIntArray sounds = new SparseIntArray();

    static {
        sounds.put(LAUGHING, R.raw.emoji_laughing);
        sounds.put(LAUGHING_LOUD, R.raw.emoji_laughing_out_loud);
        sounds.put(CHEEKY, R.raw.emoji_cheeky);
        sounds.put(CRYING, R.raw.emoji_crying);
        sounds.put(FUMING_ANGER, R.raw.emoji_fuming_anger);
        sounds.put(ASTONISH, R.raw.emoji_astonish);
        sounds.put(RELIEVED, R.raw.emoji_relieved);
        sounds.put(SLEEPY, R.raw.emoji_sleepy);
        sounds.put(SMIRKING, R.raw.emoji_smirking);
        sounds.put(THINKING, R.raw.emoji_thinking);
        sounds.put(PILE_OF_POO, R.raw.emoji_pile_of_poo);
        sounds.put(KISS, R.raw.emoji_kiss);
        sounds.put(KISSES, R.raw.emoji_kisses);
        sounds.put(KISSING, R.raw.emoji_kissing);
        sounds.put(BLOW_KISS, R.raw.emoji_blow_kiss);
        sounds.put(IN_LOVE, R.raw.emoji_in_love);
        sounds.put(HAPPINESS_WITH_LOVE, R.raw.emoji_happiness_with_love);
        sounds.put(COOL, R.raw.emoji_cool);
        sounds.put(HUNGRY, R.raw.emoji_hungry);
        sounds.put(FLIRTY, R.raw.emoji_flirty);
        sounds.put(GOBLIN, R.raw.emoji_goblin);
        sounds.put(SCREAMING_IN_SHOCK, R.raw.emoji_screaming_in_shock);
        sounds.put(ALIEN, R.raw.emoji_alien);
        sounds.put(ANGRY, R.raw.emoji_angry);
        sounds.put(SNEEZE, R.raw.emoji_sneeze);
        sounds.put(CLOWN, R.raw.emoji_clown);
        sounds.put(GHOST, R.raw.emoji_ghost);
        sounds.put(MONSTER, R.raw.emoji_monster);
        sounds.put(DEVIL, R.raw.emoji_devil);
        sounds.put(THUMBS_DOWN, R.raw.emoji_thumbs_down);
        sounds.put(THUMBS_UP, R.raw.emoji_thumbs_up);
        sounds.put(PUNCH, R.raw.emoji_punch);
        sounds.put(APPLAUSE, R.raw.emoji_applause);
        sounds.put(COUGHING, R.raw.emoji_coughing);
    }

    public static int iconOf(int emojiId){
        if(emoijs.indexOfKey(emojiId)<0)
            throw new IndexOutOfBoundsException("No emoji found for the given id");
        return emoijs.get(emojiId);
    }

    public static int soundOf(int emojiId){
        if(sounds.indexOfKey(emojiId)<0)
            throw new IndexOutOfBoundsException("No emoji found for the given id");
        return sounds.get(emojiId);
    }

    public static int[] collections = new int[]{LAUGHING,
            LAUGHING_LOUD,
            CHEEKY,
            CRYING,
            FUMING_ANGER,
            ASTONISH,
            RELIEVED,
            SLEEPY,
            SMIRKING,
            THINKING,
            PILE_OF_POO,
            KISS,
            KISSES,
            KISSING,
            BLOW_KISS,
            IN_LOVE,
            HAPPINESS_WITH_LOVE,
            COOL,
            HUNGRY,
            FLIRTY,
            GOBLIN,
            SCREAMING_IN_SHOCK,
            ALIEN,
            ANGRY,
            SNEEZE,
            CLOWN,
            GHOST,
            MONSTER,
            DEVIL,
            THUMBS_DOWN,
            THUMBS_UP,
            PUNCH,
            APPLAUSE,
            COUGHING};
}
