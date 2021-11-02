# Riverford Search Documentation:

## Installation

`$ git clone http://hayzee/recipe-search-tech-test` <br />
`$ cd recipe-search-tech-test` <br />
`$ vim resources/config.edn`

Note the last step is to configure `:dir-path` which is the directory of the recipe files to be indexed. <br />
As an alternative, recipe files can be placed in the `resources/testfiles` directory. <br />

## Usage

To run the program ensure you are in the `recipe-search-tech-test` directory and type the following:

`$ lein run`

The program will index the recipe files first, then prompt for a search term.

Either enter a search term or enter a command.

Valid commands are:

`:r` or `:reindex`  - Re-indexes the directory (for instance, in the event of new files arriving).

`:m` or `:map` - Switches to raw (i.e. displayed as a map) search results (to verify results).

`:n` or `:normal` - Switches to normal, file-list search results.

`:x` or `:exit`     - Exits the search system.

`:q` or `:quit`     - As above.

