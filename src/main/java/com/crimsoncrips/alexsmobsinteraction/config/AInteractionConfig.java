package com.crimsoncrips.alexsmobsinteraction.config;

import net.minecraftforge.fml.config.ModConfig;

import static com.mojang.text2speech.Narrator.LOGGER;

public class AInteractionConfig {
    public static boolean anacondaattackbabies = true;
    public static boolean anacondacanibalize = true;
    public static boolean baldeaglecannibalize = true;
    public static boolean bananaslugsheared = true;
    public static boolean bloodedmosquitoes = true;
    public static boolean bluejayhunt = true;
    public static boolean caimanaggresive = true;
    public static boolean capuchinhunt = true;
    public static boolean catfishcannibalize = true;
    public static boolean catfisheatstupid = false;
    public static boolean centipedelightfear = true;
    public static boolean crimsonnostraddler = true;
    public static boolean crowcannibalize = true;
    public static boolean elephantattack = true;
    public static boolean emurangedattack = true;
    public static boolean emuscuffle = true;
    public static boolean farseeralter = true;
    public static boolean farseerhumanlikeattack = true;
    public static boolean fleewarped = true;
    public static boolean flutterwither = true;
    public static boolean flyfearall = true;
    public static boolean flyfearcandles = true;
    public static boolean frogeatflies = true;
    public static boolean giantsquidcannibalize = true;
    public static boolean grizzlyattackfriendly = true;
    public static boolean gusterweighed = true;
    public static boolean hammerheadhuntmantisshrimp = true;
    public static boolean hummingbirdpolinate = true;
    public static boolean lobsternight = true;
    public static boolean mantisaggresive = true;
    public static boolean mantiscannibalize = true;
    public static boolean moscoconsume = true;
    public static boolean murmurbodykill = true;
    public static boolean nodropsforpredators = true;
    public static boolean preyfear = true;
    public static boolean raccoonhunt = true;
    public static boolean rattlesnakecannibalize = true;
    public static boolean roadrunnerday = true;
    public static boolean scourgingseas = true;
    public static boolean seagullbuff = true;
    public static boolean seagullnotsnatch = true;
    public static boolean skittishcockroaches = true;
    public static boolean snappingturtlealert = true;
    public static boolean snappingturtledormant = true;
    public static boolean spidereats = true;
    public static boolean stradllervengeance = true;
    public static boolean stradpolebobup = true;
    public static boolean sugarrush = true;
    public static boolean terrapinstomp = true;
    public static boolean tigerhuntnight = true;
    public static boolean warpedcannibalism = true;
    public static boolean weakened = true;
    public static int straddlershots = 8;
    public static boolean warpedtarantula = true;
    public static boolean cavespidereats = true;
    public static boolean emueggattack = true;

    public static boolean boneserpentfear = true;
    public static boolean mudskipperhunt = true;
    public static boolean caimaneggattack = true;
    public static boolean hummingfollowflutter = true;

    public static boolean flypester = true;
    public static boolean seagullsombrero = true;
    public static boolean enderiophageplayer = true;
    public static boolean enderioimmunity = true;
    public static boolean fluttersheared = true;
    public static boolean geladahunt = true;
    public static boolean orcahunt = true;
    public static boolean crimsontransform = true;
    public static boolean enderioadaption = true;
    public static boolean frogtransform = true;
    public static boolean snappingturtlemossincrease = true;
    public static boolean skelewagcircle = true;
    public static boolean stunnablecharge = true;
    public static boolean tusklinfulltrust = true;
    public static boolean flyconvert = true;
    public static boolean crimsonbloodprot = true;
    public static boolean snowluck = true;
    public static boolean soulbuff = true;
    public static boolean skreecherward = true;
    public static boolean sandnoammo = true;
    public static boolean lavaithanobsidianremove = true;
    public static boolean tusklintrample = true;
    public static boolean tusklinremoveequipment = true;
    public static boolean gusterprojectileprot = true;
    public static boolean elephanttrample = true;
    public static boolean voidwormstun = true;
    public static boolean straddlertroll = true;
    public static boolean aprilfools = true;

    public static boolean stradpoleflame = true;


    public static void bake() {
        try {
            stradpoleflame = ConfigHolder.INTERACT.stradpoleflame.get();
            aprilfools = ConfigHolder.INTERACT.aprilfools.get();
            straddlertroll = ConfigHolder.INTERACT.straddlertroll.get();
            voidwormstun = ConfigHolder.INTERACT.voidwormstun.get();
            elephanttrample = ConfigHolder.INTERACT.elephanttrample.get();
            gusterprojectileprot = ConfigHolder.INTERACT.gusterprojectileprot.get();
            tusklinremoveequipment = ConfigHolder.INTERACT.tusklinremoveequipment.get();
            tusklintrample = ConfigHolder.INTERACT.tusklintrample.get();
            lavaithanobsidianremove = ConfigHolder.INTERACT.lavaithanobsidianremove.get();
            sandnoammo = ConfigHolder.INTERACT.sandnoammo.get();
            skreecherward = ConfigHolder.INTERACT.skreecherward.get();
            soulbuff = ConfigHolder.INTERACT.soulbuff.get();
            snowluck = ConfigHolder.INTERACT.snowluck.get();
            crimsonbloodprot = ConfigHolder.INTERACT.crimsonbloodprot.get();
            flyconvert = ConfigHolder.INTERACT.flyconvert.get();
            tusklinfulltrust = ConfigHolder.INTERACT.tusklinfulltrust.get();
            stunnablecharge = ConfigHolder.INTERACT.stunnablecharge.get();
            skelewagcircle = ConfigHolder.INTERACT.skelewagcircle.get();
            snappingturtlemossincrease = ConfigHolder.INTERACT.snappingturtlemossincrease.get();
            frogtransform = ConfigHolder.INTERACT.frogtransform.get();
            enderioadaption = ConfigHolder.INTERACT.enderioadaption.get();
            crimsontransform = ConfigHolder.INTERACT.crimsontransform.get();
            orcahunt = ConfigHolder.INTERACT.orcahunt.get();
            flypester = ConfigHolder.INTERACT.flypester.get();
            geladahunt = ConfigHolder.INTERACT.geladahunt.get();
            seagullsombrero = ConfigHolder.INTERACT.seagullsombrero.get();
            enderiophageplayer = ConfigHolder.INTERACT.enderiophageplayer.get();
            enderioimmunity = ConfigHolder.INTERACT.enderioimmunity.get();
            fluttersheared = ConfigHolder.INTERACT.fluttersheared.get();
            anacondaattackbabies = ConfigHolder.INTERACT.anacondaattackbabies.get();
            anacondacanibalize = ConfigHolder.INTERACT.anacondacannibalize.get();
            baldeaglecannibalize = ConfigHolder.INTERACT.baldeaglecannibalize.get();
            bananaslugsheared = ConfigHolder.INTERACT.bananaslugsheared.get();
            bloodedmosquitoes = ConfigHolder.INTERACT.bloodedmosquitoes.get();
            bluejayhunt = ConfigHolder.INTERACT.bluejayhunt.get();
            caimanaggresive = ConfigHolder.INTERACT.caimanaggresive.get();
            capuchinhunt = ConfigHolder.INTERACT.capuchinhunt.get();
            catfishcannibalize = ConfigHolder.INTERACT.catfishcannibalize.get();
            catfisheatstupid = ConfigHolder.INTERACT.catfisheatstupid.get();
            centipedelightfear = ConfigHolder.INTERACT.centipedelightfear.get();
            crimsonnostraddler = ConfigHolder.INTERACT.crimsonnostraddler.get();
            crowcannibalize = ConfigHolder.INTERACT.crowcannibalize.get();
            elephantattack = ConfigHolder.INTERACT.elephantattack.get();
            elephantattack = ConfigHolder.INTERACT.emurangedattack.get();
            emuscuffle = ConfigHolder.INTERACT.emuscuffle.get();
            farseeralter = ConfigHolder.INTERACT.farseeralter.get();
            farseerhumanlikeattack = ConfigHolder.INTERACT.farseerhumanlikeattack.get();
            fleewarped = ConfigHolder.INTERACT.fleewarped.get();
            flutterwither = ConfigHolder.INTERACT.flutterwither.get();
            flyfearall = ConfigHolder.INTERACT.flyfearall.get();
            flyfearcandles = ConfigHolder.INTERACT.flyfearcandles.get();
            frogeatflies = ConfigHolder.INTERACT.frogeatflies.get();
            giantsquidcannibalize = ConfigHolder.INTERACT.giantsquidcannibalize.get();
            grizzlyattackfriendly = ConfigHolder.INTERACT.grizzlyattackfriendly.get();
            gusterweighed = ConfigHolder.INTERACT.gusterweighed.get();
            hammerheadhuntmantisshrimp = ConfigHolder.INTERACT.hammerheadhuntmantisshrimp.get();
            hummingbirdpolinate = ConfigHolder.INTERACT.hummingbirdpolinate.get();
            lobsternight = ConfigHolder.INTERACT.lobsternight.get();
            mantisaggresive = ConfigHolder.INTERACT.mantisaggresive.get();
            mantiscannibalize = ConfigHolder.INTERACT.mantiscannibalize.get();
            moscoconsume = ConfigHolder.INTERACT.moscoconsume.get();
            murmurbodykill = ConfigHolder.INTERACT.murmurbodykill.get();
            nodropsforpredators = ConfigHolder.INTERACT.nodropsforpredators.get();
            preyfear = ConfigHolder.INTERACT.preyfear.get();
            raccoonhunt = ConfigHolder.INTERACT.raccoonhunt.get();
            rattlesnakecannibalize = ConfigHolder.INTERACT.rattlesnakecannibalize.get();
            roadrunnerday = ConfigHolder.INTERACT.roadrunnerday.get();
            scourgingseas = ConfigHolder.INTERACT.scourgingseas.get();
            seagullbuff = ConfigHolder.INTERACT.seagullbuff.get();
            seagullnotsnatch = ConfigHolder.INTERACT.seagullnotsnatch.get();
            skittishcockroaches = ConfigHolder.INTERACT.skittishcockroaches.get();
            snappingturtlealert = ConfigHolder.INTERACT.snappingturtlealert.get();
            snappingturtledormant = ConfigHolder.INTERACT.snappingturtledormant.get();
            spidereats = ConfigHolder.INTERACT.spidereats.get();
            straddlershots = ConfigHolder.INTERACT.straddlershots.get();
            stradllervengeance = ConfigHolder.INTERACT.stradllervengeance.get();
            stradpolebobup = ConfigHolder.INTERACT.stradpolebobup.get();
            sugarrush = ConfigHolder.INTERACT.sugarrush.get();
            terrapinstomp = ConfigHolder.INTERACT.terrapinstomp.get();
            tigerhuntnight = ConfigHolder.INTERACT.tigerhuntnight.get();
            warpedcannibalism = ConfigHolder.INTERACT.warpedcannibalism.get();
            weakened = ConfigHolder.INTERACT.weakened.get();
            warpedtarantula = ConfigHolder.INTERACT.warpedtarantula.get();
            cavespidereats = ConfigHolder.INTERACT.cavespidereats.get();
            emueggattack = ConfigHolder.INTERACT.emueggattack.get();
            caimaneggattack = ConfigHolder.INTERACT.caimaneggattack.get();
            mudskipperhunt = ConfigHolder.INTERACT.mudskipperhunt.get();
            hummingfollowflutter = ConfigHolder.INTERACT.hummingfollowflutter.get();




        } catch (Exception e) {
            LOGGER.warn("An exception was caused trying to load the config for Alex's Interaction.");
            e.printStackTrace();
        }
    }

}
