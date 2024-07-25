# FXDex

FXDex is a project designed for easy access to information about Pokémon as they appear throughout the many mainstream pokemon games.

> [!WARNING]  
> This project is still **in heavy development.** Features can and will change at any time, until a versioning cycle is defined.

## How to Run

This project can be downloaded and run in a few steps from your command line, provided you have [Git](https://git-scm.com/downloads) installed, or another way to clone the project.

As the project does not have any releases yet, you must download the project source code as shown below.

```bash
# 1. Clone repository
git clone https://github.com/lucasstarsz/fxdex.git

# 2. Enter 'fxdex' folder
cd fxdex

# Run project from the 'gradlew' file (or gradlew.bat file for Windows users)
gradlew run
```

If you want to create an executable:
```bash
gradlew jpackage
```

> [!NOTE]
> You can generate an installer for the application by changing `skipInstaller` from `false` to `true`, inside the `jlink/jpacakge` task.

## Dependencies

- [PokeAPI](https://pokeapi.co) for all details on Pokemon.
- [JSON in Java](https://github.com/stleary/JSON-java)
- [Google Guice](https://github.com/google/guice)
- [Jakarta-Inject](https://github.com/jakartaee/inject)
- [Apache Commons-Lang](https://github.com/apache/commons-lang) & [Apache Commons Text](https://github.com/apache/commons-text)
- [atlantafx](https://github.com/mkpaz/atlantafx) for UI Theming
- [Badass JLink Plugin](https://github.com/beryx/badass-jlink-plugin) for distribution.

## License

This project is licensed under the [Apache Commons 2.0 License](LICENSE.md).