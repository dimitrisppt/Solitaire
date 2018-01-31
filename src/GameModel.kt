object GameModel {
    val deck = Deck()
    val wastePile: MutableList<Card> = mutableListOf()
    val foundationPiles: Array<FoundationPile> = arrayOf(FoundationPile(clubs), FoundationPile(diamonds),
                                                        FoundationPile(hearts), FoundationPile(spades))
    val tableauPiles = Array(7,{ TableauPile() })

    fun resetGame() {
        wastePile.clear()
        foundationPiles.forEach { it.reset() }
        deck.reset()

        tableauPiles.forEachIndexed { i, tableauPile ->
            val cardsInPile: MutableList<Card> = Array(i+1, { deck.drawCard() }).toMutableList()
            tableauPiles[i] = TableauPile(cardsInPile)
        }
    }

    fun onDeckTap() {
        if (deck.cardsInDeck.size > 0) {
            val card = deck.drawCard()
            card.faceUp = true
            wastePile.add(card)
        } else {
            deck.cardsInDeck = wastePile.toMutableList()
            wastePile.clear()
        }
    }

    fun onWasteTap() {
        if (wastePile.size > 0) {
            val card = wastePile.last()
            if (playCard(card)) {
                wastePile.remove(card)
            }
        }
    }

    fun onFoundationTap(foundationIndex: Int) {
        val foundationPile = foundationPiles[foundationIndex]
        if (foundationPile.cards.size > 0) {
            val card = foundationPile.cards.last()
            if (playCard(card)) {
                foundationPile.removeCard(card)
            }
        }
    }

    fun onTableauTap(tableauIndex: Int, cardIndex: Int) {
        val tableauPile = tableauPiles[tableauIndex]
        if (tableauPile.cards.size > 0) {
            val cards = tableauPile.cards.subList(cardIndex, tableauPile.cards.lastIndex + 1)
            if (playCards(cards)) {
                tableauPile.removeCards(cardIndex)
            }
        }
    }

    private fun playCards(cards: MutableList<Card>): Boolean {
        if (cards.size == 1) {
            return playCard(cards.first())
        } else {
            tableauPiles.forEach {
                if (it.addCards(cards)) {
                    return true
                }
            }
        }
        return false
    }

    private fun playCard(card: Card): Boolean {
        foundationPiles.forEach {
            if (it.addCard(card)) {
                return true
            }
        }
        tableauPiles.forEach {
            if (it.addCards(mutableListOf(card))) {
                return true
            }
        }
        return false

    }

    fun debugPrint() {
        var firstLine = if (wastePile.size > 0) "${wastePile.last()}" else "___"
        firstLine = firstLine.padEnd(18)
        foundationPiles.forEach {
            firstLine += if (it.cards.size > 0) "${it.cards.last()}" else "___"
            firstLine += "   "
        }
        println(firstLine)
        println()
        for (i in 0..12) {
            var row = ""
            tableauPiles.forEach {
                row += if (it.cards.size > i) "${it.cards[i]}" else "   "
                row += "   "
            }
            println(row)
        }

        val numBackUpPairsByRegion = mapOf("Africa" to 4, "Antarctica" to 7, "Asia" to 3, "Australia" to 2, "Europe" to 2, "North America" to 3, "South America" to 4)
        val region = "Antarctica"
        val area = 14000000
        val numPantsPerPanda = 1 + numBackUpPairsByRegion[region] as Int
        val howManyPants = fun(numPandas: Int) = numPandas + numPantsPerPanda
        fun countPandas(area: Int): Int = if (area > 3000) area / 25 else area / 18
        val pantsCounter = object {
            fun count(numPandas: Int, convertPandasToPants: (Int) -> Int): Int = convertPandasToPants.invoke(numPandas)
        }
        println(pantsCounter.count(countPandas(area), howManyPants))
    }
}