package com.example.medicalassesment.uIItems

interface IQuestion{
    abstract fun isMandatoryFilled(): Boolean
    abstract fun getNextFoucus(): IQuestion
    abstract fun setNextFocus(iQuestion: IQuestion)
}