# Riverford Search Documentation

## Installation

`$ git clone https://github.com/hayzee/recipe-search-tech-test.git` <br />
`$ cd recipe-search-tech-test` <br />
`$ vim resources/config.edn`

Note the last step is to configure `:dir-path` which is the directory where the recipe files are located. <br />

As an alternative, recipe files can be placed in the `resources/testfiles` directory. <br />

## Usage

To run the program ensure you are in the `recipe-search-tech-test` directory and type the following:

`$ lein run`

The program will index the recipe files first, then prompt for a search query.

Either enter a search query or enter a command.

Valid commands are:

`:r` / `:reindex` - Re-indexes the directory (for instance, in the event of new files arriving).

`:m` / `:map` - Switches to raw (i.e. displayed as a map) search results (to verify results).

`:n` / `:normal` - Switches to normal, file-list search results.

`:x` / `:exit` - Exits the search system.

`:q` / `:quit` - As above.

