# Coding Style

This document lists some less-common or unusual aspects to the codebase.

## Use ^:internal to mark internal functions.

Instead of marking a Var with `^:private` metadata, we add `^:internal`. This
makes testing easier. It signals to someone reading the code that the function
is not intended for public use.
