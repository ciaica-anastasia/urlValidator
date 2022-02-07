package UrlValidator;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.function.Predicate;

/*
    Мы стремимся сделать максимально возможную кастомизацию валидатора.
    Идея следующая:
        Если коллекция ports пустая, то пропускаем абсолютно все порты
        Если коллекция ports не пустая, то пропускаем только порты указанные в коллекции
    По аналогии для остальных коллекций (schemes, logins, passwords ...)
 */

// @NotNull надо либо использовать повсеместно, либо не использовать совсем
public final class Options{

    public Predicate<String> schemes;
    public Predicate<String> logins;
    public Predicate<String> passwords;
    public Predicate<String> hosts;
    public Predicate<String> ports;
    public Predicate<String> paths;
    public Predicate<String> queries;
    public Predicate<String> fragments;

    public Options(){
        // Default
    }

    public Options setNewSchemes(Predicate<String> schemes){
        this.schemes = schemes;
        return this;
    }

    public Options setNewLogins(Predicate<String> logins){
        this.logins = logins;
        return this;
    }

    public Options setNewPasswords(Predicate<String> passwords){
        this.passwords = passwords;
        return this;
    }

    public Options setNewHosts(Predicate<String> hosts){
        this.hosts = hosts;
        return this;
    }

    public Options setNewPorts(Predicate<String> ports){
        this.ports = ports;
        return this;
    }

    public Options setNewPaths(Predicate<String> paths){
        this.paths = paths;
        return this;
    }

    public Options setNewQueries(Predicate<String> queries){
        this.queries = queries;
        return this;
    }

    public Options setNewFragments(Predicate<String> fragments){
        this.fragments = fragments;
        return this;
    }

    @Contract(" -> new")
    public @NotNull UrlValidator build(){
        return new UrlValidator(this);
    }

}