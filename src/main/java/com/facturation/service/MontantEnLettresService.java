package com.facturation.service;

import org.springframework.stereotype.Service;

@Service
public class MontantEnLettresService {

    private static final String[] UNITES = {
            "", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf",
            "dix", "onze", "douze", "treize", "quatorze", "quinze", "seize",
            "dix-sept", "dix-huit", "dix-neuf"
    };

    private static final String[] DIZAINES = {
            "", "", "vingt", "trente", "quarante", "cinquante", "soixante",
            "soixante", "quatre-vingt", "quatre-vingt"
    };

    public String convertir(Double montant) {
        if (montant == null) {
            return "Zéro francs CFA";
        }

        long partieEntiere = montant.longValue();

        // Vérifier si le montant dépasse 999 millions
        if (partieEntiere > 999999999) {
            return "Montant trop grand";
        }

        int centimes = (int) Math.round((montant - partieEntiere) * 100);

        String resultat = convertirNombre(partieEntiere);

        // Capitaliser la première lettre
        if (!resultat.isEmpty()) {
            resultat = resultat.substring(0, 1).toUpperCase() + resultat.substring(1);
        }

        // Ajouter "francs CFA"
        if (partieEntiere == 1) {
            resultat += " franc CFA";
        } else {
            resultat += " francs CFA";
        }

        // Ajouter les centimes si nécessaire
        if (centimes > 0) {
            resultat += " et " + convertirNombre(centimes) + " centime";
            if (centimes > 1) {
                resultat += "s";
            }
        }

        return resultat;
    }

    private String convertirNombre(long nombre) {
        if (nombre == 0) {
            return "zéro";
        }

        if (nombre < 20) {
            return UNITES[(int) nombre];
        } else if (nombre < 100) {
            return convertirDizaines(nombre);
        } else if (nombre < 1000) {
            return convertirCentaines(nombre);
        } else if (nombre < 1000000) {
            return convertirMilliers(nombre);
        } else {
            return convertirMillions(nombre);
        }
    }

    private String convertirDizaines(long nombre) {
        int dizaine = (int) (nombre / 10);
        int unite = (int) (nombre % 10);

        if (dizaine == 7 || dizaine == 9) {
            dizaine--;
            unite += 10;
        }

        String resultat = DIZAINES[dizaine];

        if (unite == 0) {
            if (dizaine == 8) {
                resultat += "s";
            }
        } else if (unite == 1 && dizaine != 8) {
            resultat += " et " + UNITES[unite];
        } else if (dizaine == 8) {
            resultat += "-" + UNITES[unite];
        } else {
            resultat += "-" + UNITES[unite];
        }

        return resultat;
    }

    private String convertirCentaines(long nombre) {
        int centaines = (int) (nombre / 100);
        int reste = (int) (nombre % 100);

        String resultat;
        if (centaines == 1) {
            resultat = "cent";
        } else {
            resultat = UNITES[centaines] + " cent";
        }

        if (reste > 0) {
            resultat += " " + convertirNombre(reste);
        } else if (centaines > 1) {
            resultat += "s";
        }

        return resultat;
    }

    private String convertirMilliers(long nombre) {
        int milliers = (int) (nombre / 1000);
        int reste = (int) (nombre % 1000);

        String resultat;
        if (milliers == 1) {
            resultat = "mille";
        } else {
            resultat = convertirNombre(milliers) + " mille";
        }

        if (reste > 0) {
            resultat += " " + convertirNombre(reste);
        }

        return resultat;
    }

    private String convertirMillions(long nombre) {
        int millions = (int) (nombre / 1000000);
        int reste = (int) (nombre % 1000000);

        String resultat = convertirNombre(millions) + " million";
        if (millions > 1) {
            resultat += "s";
        }

        if (reste > 0) {
            resultat += " " + convertirNombre(reste);
        }

        return resultat;
    }
}