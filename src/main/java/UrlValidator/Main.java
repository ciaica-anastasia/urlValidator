package UrlValidator;

public class Main {
    public static void main(String[] args) {

        UrlValidator validator = new Options().setNewLogins(logins -> false).build();
        UrlValidator test1 = new Options().setNewWhosts(logins -> true).build();
        final UrlValidator test2 = new Options().build();

        //Пользователь вводит свою url вместо "URL"
        if (validator.isValid("URL")) {
            System.out.println("url is valid");
        } else {
            System.out.println("url is invalid");
        }

        // Новые примеры от Антона.
        // Под наше API я смог приспособить только один из трёх:
        /*
            URLValidation validation = new URLValidation(new URLValidation.Options()
                    .authorityValidation(new DefaultAuthorityValidation(new DomainValidation(new DomainValidation.Options().caseSensitive(true))))
            );

            URLValidation validation = new URLValidation(
                    new URLValidation.Options().schemeValidation(scheme -> scheme.equals("callto"))
            );

            URLValidation validation = new URLValidation(
                    new URLValidation.Options().allowTwoSlashes(true)
            );
        */
        UrlValidator validation = new Options().setNewSchemes(scheme -> scheme.equals("callto")).build();

        /*
        Это с последнего созвона пример от Антона:
            final UrlValidator validation = new UrlValidator(
                new UrlValidator.Options().authorityValidation(
                    new DefaultAuthorityValidation(
                        new DomainValidation(new DomainValidation.Options()
                        .tldValidation(tld -> true)
                )))
            );
        */

        //  Пример плохого URL: http://game.my.srv:19100
        // Еще один: https://стопкоронавирус.рф/?
    }
}
