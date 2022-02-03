package UrlValidator;

public class Main {
    public static void main(String[]args){

        UrlValidator validator = new UrlValidator().newOptions().allowAllLogins(false).build();

        /*
        Это с последнего созвона пример от Антона:

            Создаем класс UrlValidator передаю в него UrlValidator.Options(),
            у которого устанавливаю authorityValidation, а внутри new DomainValidation,
            который тоже с Options, где tldValidation стоит все считай true.
            Вот такой конструктор получается. Могу гибко настраивать. Могу расширить,

            final UrlValidator validation = new UrlValidator(
                new UrlValidator.Options().authorityValidation(
                    new DefaultAuthorityValidation(
                        new DomainValidation(new DomainValidation.Options()
                        .tldValidation(tld -> true)
                )))
            );
        */

        //  Пример плохого URL: http://game.my.srv:19100
    }
}
