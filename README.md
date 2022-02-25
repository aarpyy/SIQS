# **Self-Initializing Quadratic Sieve in Java**

This is an implementation of Scott Contini's Self-Initializing Quadratic Sieve. Other references, 
listed below, were used to confirm correct generation of polynomials, and PyFactorise was used
to compare results at each step of both polynomial generation and sieving during testing.

Program prints out significant elements of process, including:
- Upper limit of factor base (F)
- Sieving range (M)
- Size of factor base
- Number of primes < F
- Relations found

Program **always** prints out factor, if a non-trivial factor
is found.

## Usage
`java ./src QS/SIQS N`
- `N`: integer to be factored
### Options
- `-s`: silence all print statements **except** for non-trivial factor
- `filename`: path to file containing list of primes (default points to file containing first 1 million)

## Computation Results

| N | Factors | Time | Relations found |
| --- | --- | --- | --- |
| 1641616037791817208763207797967619321 (37 digits) | 744673529241354861493, 2204477496956597 | ~3 seconds | 315 |
| 584881463640636654453654454375396394857489 (42 digits) | 744673529241354861493, 785419973550680254573 | ~22 seconds | 537 |
| 3504362624355414643118009544796482802141562697959987 (52 digits) | 4461769171101033943441783314719, 785419973550680254573 | ~16 minutes | 1621 |

References:
[Scott Contini's Thesis](https://citeseerx.ist.psu.edu/viewdoc/download;jsessionid=53C827A542A8A950780D34E79261FF99?doi=10.1.1.26.6924&rep=rep1&type=pdf)
[Prime Wiki Article](https://www.rieselprime.de/ziki/Self-initializing_quadratic_sieve)
[PyFactorise - skollman](https://github.com/skollmann/PyFactorise/blob/master/factorise.py)