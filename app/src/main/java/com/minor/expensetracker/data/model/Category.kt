package com.minor.expensetracker.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.MiscellaneousServices
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.minor.expensetracker.ui.theme.*

data class Category(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val type: TransactionType
)

object Categories {

    val expenseCategories = listOf(
        Category("Food", Icons.Rounded.Restaurant, CategoryFood, TransactionType.EXPENSE),
        Category("Travel", Icons.Rounded.Flight, CategoryTravel, TransactionType.EXPENSE),
        Category("Shopping", Icons.Rounded.ShoppingBag, CategoryShopping, TransactionType.EXPENSE),
        Category("Entertainment", Icons.Rounded.Movie, CategoryEntertainment, TransactionType.EXPENSE),
        Category("Bills", Icons.Rounded.Receipt, CategoryBills, TransactionType.EXPENSE),
        Category("Health", Icons.Rounded.FitnessCenter, CategoryHealth, TransactionType.EXPENSE),
        Category("Education", Icons.Rounded.School, CategoryEducation, TransactionType.EXPENSE),
        Category("Transport", Icons.Rounded.DirectionsBus, CategoryTransport, TransactionType.EXPENSE),
        Category("Other", Icons.Rounded.MiscellaneousServices, CategoryOther, TransactionType.EXPENSE),
    )

    val incomeCategories = listOf(
        Category("Salary", Icons.Rounded.Payments, CategorySalary, TransactionType.INCOME),
        Category("Freelance", Icons.Rounded.Work, CategoryFreelance, TransactionType.INCOME),
        Category("Investment", Icons.Rounded.TrendingUp, CategoryInvestment, TransactionType.INCOME),
        Category("Gift", Icons.Rounded.CardGiftcard, CategoryGift, TransactionType.INCOME),
        Category("Bonus", Icons.Rounded.Cake, CategoryEntertainment, TransactionType.INCOME),
        Category("Other", Icons.Rounded.MonetizationOn, CategoryOther, TransactionType.INCOME),
    )

    val allCategories = expenseCategories + incomeCategories

    fun getCategoryByName(name: String): Category {
        return allCategories.find { it.name == name }
            ?: Category("Other", Icons.Rounded.MiscellaneousServices, CategoryOther, TransactionType.EXPENSE)
    }

    fun getCategoriesForType(type: TransactionType): List<Category> {
        return when (type) {
            TransactionType.EXPENSE -> expenseCategories
            TransactionType.INCOME -> incomeCategories
        }
    }
}
