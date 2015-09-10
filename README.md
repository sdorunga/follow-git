# follow-git

Command line app for following all unfollowed users in an organization

## Installation

Clone the repository and create an uberjar

`git clone git@github.com:sdorunga1/follow-git.git && cd follow-git`

`lein uberjar`

## Usage

You can run the jar with your github username and password along with the organization
whose members you want to follow

`java -jar target/uberjar/follow-git-0.1.0-standalone.jar <github-username> <password> <organisation>`

You can also use `lein run` instead if you don't want to compile the application

`lein run <github-username> <password> <organisation>`

## Examples

If I wanted to follow all the users of MakersAcademy I would do the following

`lein run sdorunga1 mypassword makersacademy`

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
