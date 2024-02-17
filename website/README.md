# Documentation

This directory houses the source files used for creating the documentation portion of the
[Krayon GitHub Pages website].

## Setup

The documentation website is powered by [Jekyll], the necessary prerequisites can be setup via:

```zsh
brew install ruby
```

Add the following to the end of your `~/.zshrc` file:

<details open>
<summary>Apple silicon</summary>

```zsh
if [ -d "/opt/homebrew/opt/ruby/bin" ]; then
  export PATH=/opt/homebrew/opt/ruby/bin:$PATH
  export PATH=`gem environment gemdir`/bin:$PATH
fi
```
</details>

<details>
<summary>Intel Mac</summary>

```zsh
if [ -d "/usr/local/opt/ruby/bin" ]; then
  export PATH=/usr/local/opt/ruby/bin:$PATH
  export PATH=`gem environment gemdir`/bin:$PATH
fi
```
</details>

```zsh
gem install bundler jekyll
```

## Running

```zsh
./gradlew :website:browserRun
```


[Jekyll]: https://jekyllrb.com/
[Krayon GitHub Pages website]: https://juullabs.github.io/krayon
