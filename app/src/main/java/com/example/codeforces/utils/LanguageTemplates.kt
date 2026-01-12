package com.example.codeforces.utils

import com.example.codeforces.data.model.LanguageTemplate

object LanguageTemplates {
    val templates = listOf(
        LanguageTemplate(
            id = 54,
            name = "C++ (GCC 9.2.0)",
            boilerplate = """
                #include <iostream>
                #include <vector>
                #include <algorithm>
                using namespace std;
                
                int main() {
                    // Your code here
                    
                    return 0;
                }
            """.trimIndent()
        ),
        LanguageTemplate(
            id = 62,
            name = "Java (OpenJDK 13.0.1)",
            boilerplate = """
                import java.util.*;
                import java.io.*;
                
                public class Main {
                    public static void main(String[] args) {
                        Scanner sc = new Scanner(System.in);
                        // Your code here
                        
                    }
                }
            """.trimIndent()
        ),
        LanguageTemplate(
            id = 71,
            name = "Python (3.8.1)",
            boilerplate = """
                # Your code here
                
                
            """.trimIndent()
        ),
        LanguageTemplate(
            id = 75,
            name = "C (GCC 9.2.0)",
            boilerplate = """
                #include <stdio.h>
                #include <stdlib.h>
                
                int main() {
                    // Your code here
                    
                    return 0;
                }
            """.trimIndent()
        ),
        LanguageTemplate(
            id = 63,
            name = "JavaScript (Node.js 12.14.0)",
            boilerplate = """
                const readline = require('readline');
                const rl = readline.createInterface({
                    input: process.stdin,
                    output: process.stdout
                });
                
                // Your code here
                
            """.trimIndent()
        ),
        LanguageTemplate(
            id = 70,
            name = "Python (2.7.17)",
            boilerplate = """
                # Your code here
                
                
            """.trimIndent()
        ),
        LanguageTemplate(
            id = 50,
            name = "C# (Mono 6.6.0.161)",
            boilerplate = """
                using System;
                using System.Linq;
                using System.Collections.Generic;
                
                class Program {
                    static void Main() {
                        // Your code here
                        
                    }
                }
            """.trimIndent()
        ),
        LanguageTemplate(
            id = 60,
            name = "Go (1.13.5)",
            boilerplate = """
                package main
                
                import (
                    "fmt"
                )
                
                func main() {
                    // Your code here
                    
                }
            """.trimIndent()
        ),
        LanguageTemplate(
            id = 73,
            name = "Rust (1.40.0)",
            boilerplate = """
                use std::io;
                
                fn main() {
                    // Your code here
                    
                }
            """.trimIndent()
        )
    )
    
    fun getTemplate(languageId: Int): LanguageTemplate {
        return templates.find { it.id == languageId } ?: templates[0]
    }
    
    fun getLanguageName(languageId: Int): String {
        return templates.find { it.id == languageId }?.name ?: "C++"
    }
    
    fun getLanguageExtension(languageId: Int): String {
        return when (languageId) {
            54, 75 -> "cpp"
            62 -> "java"
            71, 70 -> "py"
            63 -> "js"
            50 -> "cs"
            60 -> "go"
            73 -> "rs"
            else -> "txt"
        }
    }
}
