package UrlValidator;

public class Main {
    public static void main(String[]args){

        UrlValidator validator = new Options().setNewLogins(logins -> false).build();
        UrlValidator test = new Options().setNewHosts(logins -> true).build();
        final UrlValidator tmp = new UrlValidator( new Options() );
        //Пользователь вводит свою url вместо "URL"
        if (validator.isValid("URL")) {
            System.out.println("url is valid");
        } else {
            System.out.println("url is invalid");
        }
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
